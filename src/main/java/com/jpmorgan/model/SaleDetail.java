package com.jpmorgan.model;

public class SaleDetail {
    private Sale sale;
    private int numberOfOccurrences;

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public int getNumberOfOccurrences() {
        return numberOfOccurrences;
    }

    public void setNumberOfOccurrences(int numberOfOccurrences) {
        this.numberOfOccurrences = numberOfOccurrences;
    }

    @Override
    public String toString() {
        return "SaleDetail{" +
                "sale=" + sale +
                ", numberOfOccurrences=" + numberOfOccurrences +
                '}';
    }
}
