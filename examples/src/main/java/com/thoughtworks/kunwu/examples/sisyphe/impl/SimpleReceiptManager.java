package com.thoughtworks.kunwu.examples.sisyphe.impl;

import com.thoughtworks.kunwu.examples.sisyphe.Receipt;
import com.thoughtworks.kunwu.examples.sisyphe.SaleOrder;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.ReceiptManager;

public class SimpleReceiptManager implements ReceiptManager {
    @Override
    public Receipt generateReceipt(SaleOrder saleOrder) {
        return new Receipt(saleOrder.getCustomer().getName(), saleOrder.getSalePrice());
    }
}
