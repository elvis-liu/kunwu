package com.thoughtworks.kunwu.examples.sisyphe;

import java.util.Date;

public class SaleOrder {
    private final Book book;
    private final Customer customer;
    private final double salePrice;
    private final Date createdDate;

    public SaleOrder(Book book, Customer customer, double salePrice) {
        this.book = book;
        this.customer = customer;
        this.salePrice = salePrice;
        this.createdDate = new Date();
    }

    public Book getBook() {
        return book;
    }

    public Customer getCustomer() {
        return customer;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public String toString() {
        return "SaleOrder{" +
                "book=" + book.getName() +
                ", customer=" + customer.getName() +
                ", salePrice=" + salePrice +
                ", createdDate=" + createdDate +
                '}';
    }
}
