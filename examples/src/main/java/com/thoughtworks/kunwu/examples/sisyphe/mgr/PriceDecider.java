package com.thoughtworks.kunwu.examples.sisyphe.mgr;

import com.thoughtworks.kunwu.examples.sisyphe.Book;
import com.thoughtworks.kunwu.examples.sisyphe.Customer;

public interface PriceDecider {
    double getPrice(Book book, Customer toCustomer);
}
