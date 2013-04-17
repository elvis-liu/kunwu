package com.thoughtworks.kunwu;

import com.google.common.collect.ImmutableMap;
import com.thoughtworks.kunwu.reference.DeanReference;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeanDefinition {
    private final Class<?> targetClass;
    private DeanReference[] constructorParamRefs;
    private String deanId;
    private Map<String, DeanReference> propertyRefs = new HashMap<String, DeanReference>();

    DeanDefinition(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    Class<?> getTargetClass() {
        return targetClass;
    }

    DeanReference[] getConstructorParamRefs() {
        return constructorParamRefs;
    }

    String getDeanId() {
        if (deanId == null) {
            return getDeanDefaultName(targetClass);
        } else {
            return deanId;
        }
    }

    public Map<String, DeanReference> getPropertyRefs() {
        return propertyRefs;
    }

    public DeanDefinition constructBy(DeanReference... paramRefs) {
        constructorParamRefs = paramRefs;
        return this;
    }

    public DeanDefinition id(String deanId) {
        this.deanId = deanId;
        return this;
    }

    public DeanDefinition property(String propertyName, DeanReference ref) {
        propertyRefs.put(propertyName, ref);
        return this;
    }

    public static String getDeanDefaultName(Class<?> deanClass) {
        String className = deanClass.getSimpleName();
        return Introspector.decapitalize(className);
    }

    public static DeanDefinition copyOf(DeanDefinition from) {
        DeanDefinition copied = new DeanDefinition(from.targetClass);
        if (from.constructorParamRefs != null) {
            copied.constructorParamRefs = Arrays.copyOf(from.constructorParamRefs, from.constructorParamRefs.length);
        }
        copied.deanId = from.deanId;
        copied.propertyRefs = ImmutableMap.copyOf(from.getPropertyRefs());

        return copied;
    }
}