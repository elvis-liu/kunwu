package com.thoughtworks.kunwu.examples.sisyphe.mgr;

import com.thoughtworks.kunwu.examples.sisyphe.Receipt;
import com.thoughtworks.kunwu.examples.sisyphe.SaleOrder;

public interface ReceiptManager {
    Receipt generateReceipt(SaleOrder saleOrder);
}
