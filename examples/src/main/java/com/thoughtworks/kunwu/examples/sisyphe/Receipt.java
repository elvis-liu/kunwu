package com.thoughtworks.kunwu.examples.sisyphe;

import java.util.Date;

public class Receipt {
    private final String customerName;
    private final double salePrice;
    private final Date saleDate;

    public Receipt(String customerName, double salePrice) {
        this.customerName = customerName;
        this.salePrice = salePrice;
        this.saleDate = new Date();
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    @Override
    public String toString() {
        return "Receipt{" +
                "customerName='" + customerName + '\'' +
                ", salePrice=" + salePrice +
                ", saleDate=" + saleDate +
                '}';
    }
}
