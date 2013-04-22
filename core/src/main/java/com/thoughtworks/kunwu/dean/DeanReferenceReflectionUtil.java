package com.thoughtworks.kunwu.dean;

import com.thoughtworks.kunwu.annotation.DeanIdRef;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.thoughtworks.kunwu.dean.DeanReference.refByClass;
import static com.thoughtworks.kunwu.dean.DeanReference.refById;

public class DeanReferenceReflectionUtil {
    public static DeanReference getFieldReference(Field field) {
        DeanIdRef idAnnotation = field.getAnnotation(DeanIdRef.class);
        if (idAnnotation != null) {
            return refById(idAnnotation.value());
        } else {
            return refByClass(field.getType());
        }
    }

    public static DeanReference[] getMethodParamReferences(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        return getParamReferences(parameterTypes, parameterAnnotations);
    }

    public static DeanReference[] getConstructorParamReferences(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        return getParamReferences(parameterTypes, parameterAnnotations);
    }

    private static DeanReference[] getParamReferences(Class<?>[] parameterTypes, Annotation[][] parameterAnnotations) {
        DeanReference[] paramRefTypes = new DeanReference[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            DeanIdRef idAnnotation = findDeanIdAnnotation(parameterAnnotations[i]);
            if (idAnnotation != null) {
                paramRefTypes[i] = refById(idAnnotation.value());
            } else {
                paramRefTypes[i] = refByClass(parameterTypes[i]);
            }
        }

        return paramRefTypes;
    }

    private static DeanIdRef findDeanIdAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (DeanIdRef.class.isInstance(annotation)) {
                return DeanIdRef.class.cast(annotation);
            }
        }

        return null;
    }
}
