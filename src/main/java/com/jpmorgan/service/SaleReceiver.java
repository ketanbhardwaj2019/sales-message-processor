package com.jpmorgan.service;

import com.jpmorgan.model.Sale;
import com.jpmorgan.model.SaleAdjustment;
import com.jpmorgan.model.SaleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static java.util.stream.Collectors.*;

@Component
public class SaleReceiver {

	private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private JmsListenerEndpointRegistry registry;
	private List<SaleDetail> sales = new ArrayList<>();
	private List<SaleAdjustment> saleAdjustments = new ArrayList<>();
	private int messageCount;
	@Value("${saleReportMessageLimit:10}")
	private int saleReportMessageLimit;

	@Autowired
	private SaleReceiver(final JmsListenerEndpointRegistry registry) {
		this.registry = registry;
	}

	@JmsListener(destination = "sale_queue", containerFactory = "myFactory")
	public void receiveMessage(Sale sale) throws InterruptedException {
		LOGGER.info("Received sale <" + sale + ">");

		SaleDetail saleDetail = new SaleDetail();
		saleDetail.setSale(sale);
		saleDetail.setNumberOfOccurrences(1);

		sales.add(saleDetail);

		checkMessageCount();
	}

	@JmsListener(destination = "sale_detail_queue", containerFactory = "myFactory")
	public void receiveMessage(SaleDetail saleDetail) throws InterruptedException {
		LOGGER.info("Received sale detail <" + saleDetail + ">");

		sales.add(saleDetail);

		checkMessageCount();
	}

	@JmsListener(destination = "sale_adjustment_queue", containerFactory = "myFactory")
	public void receiveMessage(SaleAdjustment saleAdjustment) throws InterruptedException {
		saleAdjustments.add(saleAdjustment);

		LOGGER.info("Received sale adjustment <" + saleAdjustment + ">");

		switch (saleAdjustment.getOperation()) {
			case Add:
				sales
					.stream()
					.filter(saleDetail -> saleDetail.getSale().getProductType().equals(saleAdjustment.getProductType()))
					.forEach(saleDetail -> {
						saleDetail.getSale().setValue(saleDetail.getSale().getValue().add(saleAdjustment.getAdjustmentValue()));
					});
			break;

			case Subtract:
				sales
					.stream()
					.filter(saleDetail -> saleDetail.getSale().getProductType().equals(saleAdjustment.getProductType()))
					.forEach(saleDetail -> {
						saleDetail.getSale().setValue(saleDetail.getSale().getValue().subtract(saleAdjustment.getAdjustmentValue()));
					});
			break;

			case Multiply:
				sales
					.stream()
					.filter(saleDetail -> saleDetail.getSale().getProductType().equals(saleAdjustment.getProductType()))
					.forEach(saleDetail -> {
						saleDetail.getSale().setValue(saleDetail.getSale().getValue().multiply(saleAdjustment.getAdjustmentValue()));
					});
			break;
		}

		LOGGER.info("after sale adjustment <" + sales + ">");

		checkMessageCount();
	}

	private void checkMessageCount() {
		messageCount++;

		if(messageCount % 50 == 0) {
			LOGGER.info("pausing jms listener");
			registry.stop();
			System.out.println("Total adjustments made: " + saleAdjustments);
			LOGGER.info("re-starting jms listener");
			registry.start();
		}
		if(messageCount % saleReportMessageLimit == 0) {
			sales.stream()
				.skip(messageCount - saleReportMessageLimit)
				.collect(groupingBy(SaleDetail::getSale,
						summingDouble(sd -> sd.getSale().getValue().doubleValue() * sd.getNumberOfOccurrences())))
				.forEach((sale, sum) -> System.out.println(sale.getProductType() + "'s total sale is: " + sum));

			sales.stream()
					.skip(messageCount - 10)
					.collect(groupingBy(SaleDetail::getSale,
							summingInt(sd -> sd.getNumberOfOccurrences())))
					.forEach((sale, sum) -> System.out.println(sale.getProductType() + "'s total number of sales are: " + sum));
		}
	}

}
