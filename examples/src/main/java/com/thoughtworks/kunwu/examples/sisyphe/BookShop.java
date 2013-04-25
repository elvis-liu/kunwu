package com.thoughtworks.kunwu.examples.sisyphe;

import com.thoughtworks.kunwu.annotation.DeanInject;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.CustomerManager;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.PriceDecider;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.ReceiptManager;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.Storage;

import java.util.List;

public class BookShop {
    @DeanInject
    private Storage storage;

    @DeanInject
    private PriceDecider priceDecider;

    @DeanInject
    private CustomerManager customerManager;

    @DeanInject
    private ReceiptManager receiptManager;

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public void setPriceDecider(PriceDecider priceDecider) {
        this.priceDecider = priceDecider;
    }

    public void setCustomerManager(CustomerManager customerManager) {
        this.customerManager = customerManager;
    }

    public void setReceiptManager(ReceiptManager receiptManager) {
        this.receiptManager = receiptManager;
    }

    public List<Book> findBooks(String name) {
        return storage.findBooks(name);
    }

    public Customer getCustomer(String name) {
        return customerManager.getCustomer(name);
    }

    public double getSalePrice(Book book, Customer customer) {
        return priceDecider.getPrice(book, customer);
    }

    public SaleOrder generateSaleOrder(Book book, Customer customer) {
        int storageCount = storage.getStorageCount(book);
        if (storageCount == 0) {
            throw new IllegalStateException("Book out of storage!");
        }

        double salePrice = priceDecider.getPrice(book, customer);

        return new SaleOrder(book, customer, salePrice);
    }

    public Receipt processSaleOrder(SaleOrder saleOrder) {
        storage.decreaseStorageCount(saleOrder.getBook());
        customerManager.increaseScore(saleOrder);
        return receiptManager.generateReceipt(saleOrder);
    }
}
