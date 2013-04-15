package com.thoughtworks.kunwu;

import com.google.common.collect.Maps;

import java.util.Map;

public class DeanContainer {
    private Map<Class<?>, Object> deanTypeMap = Maps.newHashMap();

    public void addDean(Object dean) {
        deanTypeMap.put(dean.getClass(), dean);
    }

    public Object getDean(Class<?> targetClass) {
        return deanTypeMap.get(targetClass);
    }

    public <T> DeanBuilder<T> deanBuilder(Class<T> targetClass) {
        return new DeanBuilder<T>(this, targetClass);
    }
}
