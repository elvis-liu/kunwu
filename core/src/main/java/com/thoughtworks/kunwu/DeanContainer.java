package com.thoughtworks.kunwu;

import com.google.common.collect.Maps;

import java.util.Map;

public class DeanContainer {
    private Map<String, DeanDefinition> deanIdDefinitionMap = Maps.newHashMap();

    public Object getDeanInstance(String id) {
        return getDeanInstance(id, Object.class);
    }

    public <T> T getDeanInstance(String id, Class<T> type) {
        DeanDefinition deanDefinition = deanIdDefinitionMap.get(id);
        if (deanDefinition == null) {
            throw new IllegalArgumentException("No Dean defined for given id: " + id);
        }

        DeanBuilder deanBuilder = new DeanBuilder(this, deanDefinition);
        return type.cast(deanBuilder.create());
    }

    public String addDean(DeanDefinition deanDefinition) {
        String id = deanDefinition.getDeanId();
        if (deanIdDefinitionMap.containsKey(id)) {
            throw new IllegalArgumentException("Dean of given id already exists: " + id);
        }

        deanIdDefinitionMap.put(id, DeanDefinition.copyOf(deanDefinition));
        return id;
    }
}
