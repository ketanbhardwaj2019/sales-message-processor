package com.jpmorgan.integrationtest;

import com.jpmorgan.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

@SpringBootTest
public class SaleIntegrationTest {

    @Autowired
    JmsTemplate jmsTemplate;

    @Test
    @DirtiesContext
    public void testSale() {
        System.out.println("Running testSale()");

        Sale sale = new Sale();
        sale.setProductType(ProductType.Apple);
        sale.setValue(new BigDecimal(66));

        jmsTemplate.convertAndSend("sale_queue", sale);

    }

    @Test
    @DirtiesContext
    public void testSaleDetail() throws InterruptedException {
        System.out.println("Running testSaleDetail()");

        Sale sale = new Sale();
        sale.setProductType(ProductType.Apple);
        sale.setValue(new BigDecimal(66));

        SaleDetail saleDetail = new SaleDetail();
        saleDetail.setSale(sale);
        saleDetail.setNumberOfOccurrences(3);

        jmsTemplate.convertAndSend("sale_detail_queue", saleDetail);

        Thread.sleep(1000);
    }

    @Test
    @DirtiesContext
    public void testSaleReportFor10Messages() throws InterruptedException {
        System.out.println("Running testSaleReportFor10Messages()");

        Sale sale = new Sale();
        sale.setProductType(ProductType.Apple);
        sale.setValue(new BigDecimal(66));

        for(int i=0; i< 10; i++) {
            jmsTemplate.convertAndSend("sale_queue", sale);
        }
        Thread.sleep(1000);
    }

    @Test
    @DirtiesContext
    public void testSaleReportFor50Messages() throws InterruptedException {
        System.out.println("Running testSaleReportFor50Messages()");

        Sale sale = new Sale();
        sale.setProductType(ProductType.Apple);
        sale.setValue(new BigDecimal(66));

        SaleAdjustment saleAdjustment1 = new SaleAdjustment();
        saleAdjustment1.setProductType(ProductType.Apple);
        saleAdjustment1.setOperation(Operation.Subtract);
        saleAdjustment1.setAdjustmentValue(new BigDecimal(60));

        SaleAdjustment saleAdjustment2 = new SaleAdjustment();
        saleAdjustment2.setProductType(ProductType.Apple);
        saleAdjustment2.setOperation(Operation.Add);
        saleAdjustment2.setAdjustmentValue(new BigDecimal(60));

        for(int i=0; i < 49; i++) {
            jmsTemplate.convertAndSend("sale_queue", sale);
        }
        Thread.sleep(1000);
        jmsTemplate.convertAndSend("sale_adjustment_queue", saleAdjustment1);
        jmsTemplate.convertAndSend("sale_adjustment_queue", saleAdjustment2);

    }
}
