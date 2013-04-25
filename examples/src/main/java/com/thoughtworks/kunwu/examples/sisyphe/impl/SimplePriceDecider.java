package com.thoughtworks.kunwu.examples.sisyphe.impl;

import com.thoughtworks.kunwu.examples.sisyphe.Book;
import com.thoughtworks.kunwu.examples.sisyphe.Customer;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.PriceDecider;

public class SimplePriceDecider implements PriceDecider {
    @Override
    public double getPrice(Book book, Customer toCustomer) {
        return book.getPrice() / 2;
    }
}
