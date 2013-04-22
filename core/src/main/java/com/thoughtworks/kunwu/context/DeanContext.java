package com.thoughtworks.kunwu.context;

import com.thoughtworks.kunwu.dean.DeanDefinition;
import com.thoughtworks.kunwu.exception.NoSuchDeanException;

public interface DeanContext {
    Object getDeanInstance(String id) throws NoSuchDeanException;

    <T> T getDeanInstance(String id, Class<T> type) throws NoSuchDeanException;

    DeanDefinition getDeanDefinition(String id) throws NoSuchDeanException;
}
