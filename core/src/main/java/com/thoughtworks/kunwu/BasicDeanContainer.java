package com.thoughtworks.kunwu;

import com.google.common.collect.Maps;

import java.util.Map;

public class BasicDeanContainer implements DeanContainer {
    private Map<String, DeanDefinition> deanIdDefinitionMap = Maps.newHashMap();

    @Override
    public Object getDeanInstance(String id) {
        return getDeanInstance(id, Object.class);
    }

    @Override
    public <T> T getDeanInstance(String id, Class<T> type) {
        DeanDefinition deanDefinition = deanIdDefinitionMap.get(id);
        if (deanDefinition == null) {
            throw new IllegalArgumentException("No Dean defined for given id: " + id);
        }

        DeanInstanceBuilder deanInstanceBuilder = new DeanInstanceBuilder(this, deanDefinition);
        return type.cast(deanInstanceBuilder.buildInstance());
    }

    @Override
    public String addDean(DeanDefinition deanDefinition) {
        String id = deanDefinition.getDeanId();
        if (deanIdDefinitionMap.containsKey(id)) {
            throw new IllegalArgumentException("Dean of given id already exists: " + id);
        }

        deanIdDefinitionMap.put(id, DeanDefinition.copyOf(deanDefinition));
        return id;
    }
}
