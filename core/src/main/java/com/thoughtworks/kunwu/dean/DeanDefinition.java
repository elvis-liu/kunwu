package com.thoughtworks.kunwu.dean;

import com.google.common.collect.ImmutableMap;

import java.beans.Introspector;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeanDefinition {
    public static final DeanScope DEFAULT_SCOPE = DeanScope.SINGLETON;

    private final Class<?> targetClass;
    private DeanReference[] constructorParamRefs;
    private String deanId;
    private Map<String, DeanReference> propertyRefs = new HashMap<String, DeanReference>();
    private DeanScope scope;

    public DeanDefinition(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public DeanReference[] getConstructorParamRefs() {
        return constructorParamRefs;
    }

    public String getDeanId() {
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

    public DeanDefinition scope(String scopeName) {
        return scope(DeanScope.scopeNameOf(scopeName));
    }

    public DeanDefinition scope(DeanScope scope) {
        this.scope = scope;
        return this;
    }

    public static String getDeanDefaultName(Class<?> deanClass) {
        String className = deanClass.getSimpleName();
        return Introspector.decapitalize(className);
    }

    public DeanScope getScope() {
        if (scope == null) {
            return DEFAULT_SCOPE;
        } else {
            return scope;
        }
    }

    public static DeanDefinition copyOf(DeanDefinition from) {
        DeanDefinition copied = new DeanDefinition(from.targetClass);
        if (from.constructorParamRefs != null) {
            copied.constructorParamRefs = Arrays.copyOf(from.constructorParamRefs, from.constructorParamRefs.length);
        }
        copied.deanId = from.deanId;
        copied.propertyRefs = ImmutableMap.copyOf(from.getPropertyRefs());
        copied.scope = from.scope;

        return copied;
    }
}