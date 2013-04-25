package com.thoughtworks.kunwu.examples.sisyphe.config.test;

import com.google.common.collect.Lists;
import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DefineDean;
import com.thoughtworks.kunwu.annotation.ReturnDean;
import com.thoughtworks.kunwu.dean.DeanDefinition;
import com.thoughtworks.kunwu.examples.sisyphe.*;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.CustomerManager;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.PriceDecider;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.ReceiptManager;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.Storage;

import java.util.List;

@DeanConfig
public class TestConfig {
    @DefineDean("bookShop")
    public DeanDefinition defineBookShop() {
        return DeanDefinition.defineDeanByAnnotation(BookShop.class);
    }

    @ReturnDean("customerManager")
    public CustomerManager createCustomerManager() {
        return new CustomerManager() {
            @Override
            public Customer getCustomer(String name) {
                Customer customer = new Customer();
                customer.setName("test");
                return customer;
            }

            @Override
            public void increaseScore(SaleOrder saleOrder) {
            }
        };
    }

    @ReturnDean("receiptManager")
    public ReceiptManager createReceiptManager() {
        return new ReceiptManager() {
            @Override
            public Receipt generateReceipt(SaleOrder saleOrder) {
                return new Receipt("test", 10);
            }
        };
    }

    @ReturnDean("priceDecider")
    public PriceDecider createPriceDecider() {
        return new PriceDecider() {
            @Override
            public double getPrice(Book book, Customer toCustomer) {
                return 10;
            }
        };
    }

    @ReturnDean("storage")
    public Storage createStorage() {
        return new Storage() {
            @Override
            public List<Book> findBooks(String name) {
                Book book = new Book();
                book.setName("test_book");
                book.setPrice(20);
                return Lists.newArrayList(book);
            }

            @Override
            public int getStorageCount(Book book) {
                return 1;
            }

            @Override
            public void decreaseStorageCount(Book book) {
            }
        };
    }
}
