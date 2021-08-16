package com.jpmorgan.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class SaleAdjustment {
    private ProductType productType;
    private BigDecimal adjustmentValue;
    private Operation operation;

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public BigDecimal getAdjustmentValue() {
        return adjustmentValue.round(new MathContext(4, RoundingMode.HALF_UP));
    }

    public void setAdjustmentValue(BigDecimal adjustmentValue) {
        this.adjustmentValue = adjustmentValue;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "SaleAdjustment{" +
                "productType=" + productType +
                ", adjustmentValue=" + adjustmentValue +
                ", operation=" + operation +
                '}';
    }
}
