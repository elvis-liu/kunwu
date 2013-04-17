package com.thoughtworks.kunwu;

public interface DeanContainer {
    Object getDeanInstance(String id);

    <T> T getDeanInstance(String id, Class<T> type);

    String addDean(DeanDefinition deanDefinition);
}
