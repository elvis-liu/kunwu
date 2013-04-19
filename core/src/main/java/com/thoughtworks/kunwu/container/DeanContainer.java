package com.thoughtworks.kunwu.container;

import com.thoughtworks.kunwu.dean.DeanDefinition;

public abstract class DeanContainer {
    public abstract Object getDeanInstance(String id);

    public <T> T getDeanInstance(String id, Class<T> type) {
        return type.cast(getDeanInstance(id));
    }

    public abstract String addDeanDefinition(DeanDefinition deanDefinition);

    public abstract DeanDefinition getDeanDefinition(String id);
}
