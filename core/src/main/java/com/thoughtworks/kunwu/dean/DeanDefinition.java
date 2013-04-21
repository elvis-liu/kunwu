package com.thoughtworks.kunwu.dean;

import com.google.common.collect.ImmutableMap;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeanDefinition {
    public static final DeanScope DEFAULT_SCOPE = DeanScope.SINGLETON;

    private final Class<?> targetClass;
    private DeanReference[] constructorParamRefs;
    private Constructor<?> constructor;
    private String deanId;
    private Map<String, DeanReference> propertyRefMap = new HashMap<String, DeanReference>();
    private DeanScope scope;

    private DeanDefinition(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public static DeanDefinition defineDean(Class<?> targetClass) {
        return new DeanDefinition(targetClass);
    }

    public static DeanDefinition defineDeanByAnnotation(Class<?> targetClass) {
        return new AnnotationBasedDeanDefinitionParser(targetClass).parse();
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

    public Map<String, DeanReference> getPropertyRefMap() {
        return propertyRefMap;
    }

    public DeanScope getScope() {
        if (scope == null) {
            return DEFAULT_SCOPE;
        } else {
            return scope;
        }
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public DeanDefinition constructorParams(DeanReference... paramRefs) {
        if (this.constructorParamRefs != null) {
            throw new IllegalArgumentException("Already defined constructor params");
        }
        this.constructorParamRefs = paramRefs;
        return this;
    }

    public DeanDefinition constructor(Constructor<?> constructor, DeanReference[] paramRefs) {
        if (this.constructor != null) {
            throw new IllegalArgumentException("Already defined constructor");
        }
        if (this.constructorParamRefs != null) {
            throw new IllegalArgumentException("Already defined constructor params");
        }
        this.constructor = constructor;
        this.constructorParamRefs = paramRefs;
        return this;
    }

    public DeanDefinition id(String deanId) {
        if (this.deanId != null) {
            throw new IllegalArgumentException("Already defined id");
        }
        this.deanId = deanId;
        return this;
    }

    public DeanDefinition property(String propertyName, DeanReference ref) {
        propertyRefMap.put(propertyName, ref);
        return this;
    }

    public DeanDefinition scope(String scopeName) {
        return scope(DeanScope.scopeNameOf(scopeName));
    }

    public DeanDefinition scope(DeanScope scope) {
        if (this.scope != null) {
            throw new IllegalArgumentException("Already defined scope");
        }
        this.scope = scope;
        return this;
    }

    public static String getDeanDefaultName(Class<?> deanClass) {
        String className = deanClass.getSimpleName();
        return Introspector.decapitalize(className);
    }

    public static DeanDefinition copyOf(DeanDefinition from) {
        DeanDefinition copied = defineDean(from.targetClass);
        if (from.constructorParamRefs != null) {
            copied.constructorParamRefs = Arrays.copyOf(from.constructorParamRefs, from.constructorParamRefs.length);
        }
        copied.deanId = from.deanId;
        copied.propertyRefMap = ImmutableMap.copyOf(from.propertyRefMap);
        copied.scope = from.scope;
        copied.constructor = from.constructor;

        return copied;
    }
}