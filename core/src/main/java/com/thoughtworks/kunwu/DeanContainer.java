package com.thoughtworks.kunwu;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

import static com.thoughtworks.kunwu.CollectionUtils.arrayFilter;
import static com.thoughtworks.kunwu.CollectionUtils.arrayTransform;

public class DeanContainer {
    private Map<Class<?>, Object> deanTypeMap = Maps.newHashMap();

    public void addDean(Object dean) {
        deanTypeMap.put(dean.getClass(), dean);
    }

    public <T> T create(Class<T> targetClass, Class<?>... paramClasses) {
        if (deanTypeMap.containsKey(targetClass)) {
            return targetClass.cast(deanTypeMap.get(targetClass));
        }

        Constructor<T>[] matchedConstructors = findMatchedConstructors(targetClass, paramClasses);
        if (matchedConstructors.length > 1) {
            throw new IllegalArgumentException("Cannot determine which constructor to use!");
        }

        if (matchedConstructors.length == 0) {
            throw new IllegalArgumentException("Cannot find matched constructor!");
        }

        T createObj = createNewInstance(matchedConstructors[0]);
        deanTypeMap.put(targetClass, createObj);
        return createObj;
    }

    private <T> Constructor<T>[] findMatchedConstructors(Class<T> targetClass, final Class<?>[] paramClasses) {
        Constructor<T>[] allConstructors = (Constructor<T>[]) targetClass.getConstructors();

        return arrayFilter(allConstructors, new Predicate<Constructor<T>>() {
            @Override
            public boolean apply(Constructor<T> input) {
                return Arrays.equals(paramClasses, input.getParameterTypes());
            }
        });
    }

    private <T> T createNewInstance(Constructor<T> constructor) {
        Object[] params = arrayTransform(constructor.getParameterTypes(), new Function<Class<?>, Object>() {
            @Override
            public Object apply(java.lang.Class<?> input) {
                Object param = deanTypeMap.get(input);
                if (param == null) {
                    throw new IllegalArgumentException("No matched Dean to new instance!");
                }
                return param;
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

    public Object getDean(Class<?> targetClass) {
        return deanTypeMap.get(targetClass);
    }
}
