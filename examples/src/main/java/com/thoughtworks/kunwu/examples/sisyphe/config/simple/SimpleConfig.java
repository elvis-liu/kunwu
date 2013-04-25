package com.thoughtworks.kunwu.examples.sisyphe.config.simple;

import com.google.common.collect.Lists;
import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.DefineDean;
import com.thoughtworks.kunwu.annotation.ReturnDean;
import com.thoughtworks.kunwu.dean.DeanDefinition;
import com.thoughtworks.kunwu.examples.sisyphe.Book;
import com.thoughtworks.kunwu.examples.sisyphe.BookShop;
import com.thoughtworks.kunwu.examples.sisyphe.Customer;
import com.thoughtworks.kunwu.examples.sisyphe.impl.SimpleCustomerManager;
import com.thoughtworks.kunwu.examples.sisyphe.impl.SimplePriceDecider;
import com.thoughtworks.kunwu.examples.sisyphe.impl.SimpleReceiptManager;
import com.thoughtworks.kunwu.examples.sisyphe.impl.SimpleStorage;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.CustomerManager;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.PriceDecider;

import java.util.List;

import static com.thoughtworks.kunwu.dean.DeanDefinition.defineDean;
import static com.thoughtworks.kunwu.dean.DeanDefinition.defineDeanByAnnotation;

@DeanConfig
public class SimpleConfig {
    @ReturnDean("allCustomers")
    public List<Customer> allCustomers() {
        Customer customer1 = new Customer();
        customer1.setName("test1");
        Customer customer2 = new Customer();
        customer2.setName("test2");
        return Lists.newArrayList(customer1, customer2);
    }

    @ReturnDean("allBooks")
    public List<Book> allBooks() {
        return Lists.newArrayList(
                createBook("book1", 10),
                createBook("book2", 5),
                createBook("book3", 20));
    }

    private Book createBook(String name, int price) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        return book;
    }

    @DefineDean("bookShop")
    public DeanDefinition defineBookShop() {
        return defineDeanByAnnotation(BookShop.class);
    }

    @ReturnDean("customerManager")
    public CustomerManager createCustomerManager(@DeanIdRef("allCustomers") List<Customer> allCustomers) {
        return new SimpleCustomerManager(allCustomers);
    }

    @DefineDean("receiptManager")
    public DeanDefinition createReceiptManager() {
        return defineDean(SimpleReceiptManager.class);
    }

    @ReturnDean("priceDecider")
    public PriceDecider createPriceDecider() {
        return new SimplePriceDecider();
    }

    @DefineDean("storage")
    public DeanDefinition createStorage() {
        return defineDeanByAnnotation(SimpleStorage.class);
    }
}
