package com.thoughtworks.kunwu.examples.sisyphe.mgr;

import com.thoughtworks.kunwu.examples.sisyphe.Customer;
import com.thoughtworks.kunwu.examples.sisyphe.SaleOrder;

public interface CustomerManager {
    Customer getCustomer(String name);

    void increaseScore(SaleOrder saleOrder);
}
