package com.thoughtworks.kunwu.examples.sisyphe.impl;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.DeanInject;
import com.thoughtworks.kunwu.examples.sisyphe.Book;
import com.thoughtworks.kunwu.examples.sisyphe.mgr.Storage;
import sun.org.mozilla.javascript.internal.Function;

import java.util.List;

public class SimpleStorage implements Storage {
    @DeanInject
    @DeanIdRef("allBooks")
    private List<Book> allBooks;

    public void setAllBooks(List<Book> allBooks) {
        this.allBooks = allBooks;
    }

    @Override
    public List<Book> findBooks(final String name) {
        return Lists.newArrayList(Iterables.filter(allBooks, new Predicate<Book>() {
            @Override
            public boolean apply(Book input) {
                return input.getName().contains(name);
            }
        }));
    }

    @Override
    public int getStorageCount(Book book) {
        return 1;
    }

    @Override
    public void decreaseStorageCount(Book book) {
    }
}
