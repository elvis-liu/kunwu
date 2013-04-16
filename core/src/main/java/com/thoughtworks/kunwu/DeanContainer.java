package com.thoughtworks.kunwu;

import com.google.common.collect.Maps;

import java.util.Map;

public class DeanContainer {
    private Map<String, Object> deanIdMap = Maps.newHashMap();

    public void addDean(String id, Object dean) {
        if (deanIdMap.containsKey(id)) {
            throw new IllegalArgumentException("Dean of given id already exists: " + id);
        }
        deanIdMap.put(id, dean);
    }

    public Object getDean(String id) {
        return deanIdMap.get(id);
    }

    public <T> DeanBuilder<T> deanBuilder(Class<T> targetClass) {
        return new DeanBuilder<T>(this, targetClass);
    }
}
