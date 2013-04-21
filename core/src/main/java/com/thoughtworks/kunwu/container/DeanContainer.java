package com.thoughtworks.kunwu.container;

import com.thoughtworks.kunwu.context.DeanContext;
import com.thoughtworks.kunwu.dean.DeanDefinition;

public abstract class DeanContainer implements DeanContext {

    @Override
    public <T> T getDeanInstance(String id, Class<T> type) {
        return type.cast(getDeanInstance(id));
    }

    public abstract String addDeanDefinition(DeanDefinition deanDefinition);
    public abstract void addDeanInstance(String id, Object deanInstance);
}
