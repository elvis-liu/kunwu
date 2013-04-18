package com.thoughtworks.kunwu;

public abstract class DeanContainer {
    public abstract Object getDeanInstance(String id);

    public <T> T getDeanInstance(String id, Class<T> type) {
        return type.cast(getDeanInstance(id));
    }

    public abstract String addDeanDefinition(DeanDefinition deanDefinition);

    public abstract DeanDefinition getDeanDefinition(String id);
}
