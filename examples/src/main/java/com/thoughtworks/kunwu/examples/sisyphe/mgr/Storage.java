package com.thoughtworks.kunwu.examples.sisyphe.mgr;

import com.thoughtworks.kunwu.examples.sisyphe.Book;

import java.util.List;

public interface Storage {
    List<Book> findBooks(String name);

    int getStorageCount(Book book);

    void decreaseStorageCount(Book book);
}
