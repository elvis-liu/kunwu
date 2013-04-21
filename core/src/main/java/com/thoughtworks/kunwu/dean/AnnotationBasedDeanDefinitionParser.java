package com.thoughtworks.kunwu.dean;

import com.thoughtworks.kunwu.annotation.DeanId;
import com.thoughtworks.kunwu.annotation.DeanInject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static com.thoughtworks.kunwu.dean.DeanReference.refByClass;
import static com.thoughtworks.kunwu.dean.DeanReference.refById;

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
                DeanId idAnnotation = field.getAnnotation(DeanId.class);
                if (idAnnotation != null) {
                    deanDefinition.property(field.getName(), refById(idAnnotation.value()));
                } else {
                    deanDefinition.property(field.getName(), refByClass(field.getType()));
                }
            }
        }
    }

    private void parseConstructorAnnotations(DeanDefinition deanDefinition) {
        Constructor<?>[] constructors = targetClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            DeanInject annotation = constructor.getAnnotation(DeanInject.class);
            if (annotation != null) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
                DeanReference[] paramRefTypes = new DeanReference[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    DeanId idAnnotation = findDeanIdAnnotation(parameterAnnotations[i]);
                    if (idAnnotation != null) {
                        paramRefTypes[i] = refById(idAnnotation.value());
                    } else {
                        paramRefTypes[i] = refByClass(parameterTypes[i]);
                    }
                }
                deanDefinition.constructor(constructor, paramRefTypes);
            }
        }
    }

    private static DeanId findDeanIdAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (DeanId.class.isInstance(annotation)) {
                return DeanId.class.cast(annotation);
            }
        }

        return null;
    }
}
