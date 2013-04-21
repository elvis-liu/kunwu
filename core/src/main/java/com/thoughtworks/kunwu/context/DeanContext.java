package com.thoughtworks.kunwu.context;

import com.thoughtworks.kunwu.dean.DeanDefinition;

public interface DeanContext {
    Object getDeanInstance(String id);

    <T> T getDeanInstance(String id, Class<T> type);

    DeanDefinition getDeanDefinition(String id);
}
