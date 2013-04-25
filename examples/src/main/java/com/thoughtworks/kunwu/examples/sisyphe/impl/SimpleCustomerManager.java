package com.thoughtworks.kunwu.examples.sisyphe.impl;

import com.thoughtworks.kunwu.examples.sisyphe.Customer;
import com.thoughtworks.kunwu.examples.sisyphe.SaleOrder;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.CustomerManager;

import java.util.List;

public class SimpleCustomerManager implements CustomerManager {
    private List<Customer> allCustomers;

    public SimpleCustomerManager(List<Customer> customerList) {
        this.allCustomers = customerList;
    }

    @Override
    public Customer getCustomer(String name) {
        for (Customer customer : allCustomers) {
            if (customer.getName().equals(name)) {
                return customer;
            }
        }

        throw new IllegalArgumentException("No customer with name: " + name);
    }

    @Override
    public void increaseScore(SaleOrder saleOrder) {
    }
}
