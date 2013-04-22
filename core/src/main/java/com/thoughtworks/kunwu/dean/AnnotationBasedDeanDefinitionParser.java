package com.thoughtworks.kunwu.dean;

import com.thoughtworks.kunwu.annotation.DeanInject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static com.thoughtworks.kunwu.dean.DeanReferenceReflectionUtil.getConstructorParamReferences;
import static com.thoughtworks.kunwu.dean.DeanReferenceReflectionUtil.getFieldReference;

class AnnotationBasedDeanDefinitionParser {
    private final Class<?> targetClass;

    public AnnotationBasedDeanDefinitionParser(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public DeanDefinition parse() {
        DeanDefinition deanDefinition = DeanDefinition.defineDean(targetClass);
        parseConstructorAnnotations(deanDefinition);
        parsePropertyAnnotations(deanDefinition);

        return deanDefinition;
    }

    private void parsePropertyAnnotations(DeanDefinition deanDefinition) {
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field field : declaredFields) {
            DeanInject annotation = field.getAnnotation(DeanInject.class);
            if (annotation != null) {
                deanDefinition.property(field.getName(), getFieldReference(field));
            }
        }
    }

    private void parseConstructorAnnotations(DeanDefinition deanDefinition) {
        Constructor<?>[] constructors = targetClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            DeanInject annotation = constructor.getAnnotation(DeanInject.class);
            if (annotation != null) {
                deanDefinition.constructor(constructor, getConstructorParamReferences(constructor));
            }
        }
    }
}
