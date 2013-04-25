package com.thoughtworks.kunwu.examples.sisyphe;

import com.google.common.collect.Sets;
import com.thoughtworks.kunwu.context.PackageBasedDeanContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ExampleRunner {
    public static void main(String[] args) {
        String configPkg = "com.thoughtworks.kunwu.examples.sisyphe.config.simple";
        saleBookProcedure(configPkg);
    }

    private static void saleBookProcedure(String configPkg) {
        try {
            PackageBasedDeanContext deanContext = new PackageBasedDeanContext(
                    Sets.newHashSet(configPkg));
            deanContext.scanAll();
            BookShop bookShop = deanContext.getDeanInstance("bookShop", BookShop.class);
            System.out.println("Customer name: ");
            String customerName = readLine();
            Customer customer = bookShop.getCustomer(customerName);
            System.out.println("Book name: ");
            String bookName = readLine();
            List<Book> bookList = bookShop.findBooks(bookName);
            Book book = bookList.get(0);
            System.out.println("Found book: " + book.toString());
            System.out.println("Sale price for " + customer.getName() + " :" + bookShop.getSalePrice(book, customer));
            System.out.println("Order? (yes/no): ");
            String decision = readLine();
            if ("yes".equalsIgnoreCase(decision)) {
                SaleOrder saleOrder = bookShop.generateSaleOrder(book, customer);
                System.out.println("Confirm order (yes/no): " + saleOrder.toString());
                String confirm = readLine();
                if ("yes".equalsIgnoreCase(confirm)) {
                    Receipt receipt = bookShop.processSaleOrder(saleOrder);
                    System.out.println("Book sold! Receipt: " + receipt.toString());
                }
            }
        } catch (Exception e) {
            System.out.println("Failed due to: " + e.getMessage());
        }
    }

    private static String readLine() throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        return br.readLine();
    }
}
