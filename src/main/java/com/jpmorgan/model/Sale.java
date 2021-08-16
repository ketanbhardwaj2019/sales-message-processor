package com.jpmorgan.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

public class Sale {
    private ProductType productType;
    private BigDecimal value;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public BigDecimal getValue() {
        return value.round(new MathContext(4, RoundingMode.HALF_UP));
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "productType=" + productType +
                ", value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sale)) return false;
        Sale sale = (Sale) o;
        return getProductType() == sale.getProductType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductType());
    }
}
