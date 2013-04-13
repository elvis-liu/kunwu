package com.thoughtworks.kunwu;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class DeanContainer {
    private Map<Class<?>, Object> deanTypeMap = Maps.newHashMap();

    public void addDean(Object dean) {
        deanTypeMap.put(dean.getClass(), dean);
    }

    public <T> T create(Class<T> targetClass) {
        if (deanTypeMap.containsKey(targetClass)) {
            return (T) deanTypeMap.get(targetClass);
        }

        Constructor<T>[] constructors = (Constructor<T>[]) targetClass.getConstructors();
        for (Constructor<T> constructor : constructors) {
            if (checkIfCanBeContructed(constructor)) {
                T createObj = createNewInstance(constructor);
                deanTypeMap.put(targetClass, createObj);
                return createObj;
            }
        }

        return null;
    }

    private <T> T createNewInstance(Constructor<T> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] params = arrayTransform(constructor.getParameterTypes(), new Function<Class<?>, Object>() {
            @Override
            public Object apply(java.lang.Class<?> input) {
                return deanTypeMap.get(input);
            }
        });

        try {
            return constructor.newInstance(params);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    private <T> boolean checkIfCanBeContructed(Constructor<T> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (Class<?> paramType : parameterTypes) {
            if (!deanTypeMap.containsKey(paramType)) {
                return false;
            }
        }

        return true;
    }

    private static <F, T> T[] arrayTransform(F[] inputArray, Function<F,T> func) {
        T[] targetArray = (T[]) new Object[inputArray.length];
        for (int i = 0; i < inputArray.length; i++) {
            T target = func.apply(inputArray[i]);
            targetArray[i] = target;
        }

        return targetArray;
    }

    public Object getDean(Class<?> targetClass) {
        return deanTypeMap.get(targetClass);
    }
}
