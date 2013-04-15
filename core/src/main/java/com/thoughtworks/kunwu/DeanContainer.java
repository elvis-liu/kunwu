package com.thoughtworks.kunwu;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.thoughtworks.kunwu.reference.DeanReference;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

import static com.thoughtworks.kunwu.CollectionUtils.arrayFilter;

public class DeanContainer {
    private Map<Class<?>, Object> deanTypeMap = Maps.newHashMap();

    public void addDean(Object dean) {
        deanTypeMap.put(dean.getClass(), dean);
    }

    public <T> T create(Class<T> targetClass, DeanReference... paramRefs) {
        if (deanTypeMap.containsKey(targetClass)) {
            return targetClass.cast(deanTypeMap.get(targetClass));
        }

        Constructor<T>[] matchedConstructors = findMatchedConstructors(targetClass, paramRefs);
        if (matchedConstructors.length > 1) {
            throw new IllegalArgumentException("Cannot determine which constructor to use!");
        }

        if (matchedConstructors.length == 0) {
            throw new IllegalArgumentException("Cannot find matched constructor!");
        }

        T createObj = createNewInstance(matchedConstructors[0], paramRefs);
        deanTypeMap.put(targetClass, createObj);
        return createObj;
    }

    private <T> Constructor<T>[] findMatchedConstructors(Class<T> targetClass, final DeanReference[] paramRefs) {
        Constructor<T>[] allConstructors = (Constructor<T>[]) targetClass.getConstructors();
        final Class<?>[] paramTypes = getParameterTypesFromRefs(paramRefs);

        return arrayFilter(allConstructors, new Predicate<Constructor<T>>() {
            @Override
            public boolean apply(Constructor<T> input) {
                return Arrays.equals(paramTypes, input.getParameterTypes());
            }
        });
    }

    private Class<?>[] getParameterTypesFromRefs(DeanReference[] paramRefs) {
        final Class<?>[] paramTypes = new Class<?>[paramRefs.length];
        for (int i = 0; i < paramRefs.length; i++) {
            paramTypes[i] = paramRefs[i].getClassType();
        }
        return paramTypes;
    }

    private <T> T createNewInstance(Constructor<T> constructor, DeanReference[] paramRefs) {
        Object[] parameters = new Object[paramRefs.length];
        for (int i = 0; i < paramRefs.length; i++) {
            Object param;
            switch (paramRefs[i].getRefType()) {
                case CLASS: {
                    param = deanTypeMap.get(paramRefs[i].getClassType());
                    if (param == null) {
                        throw new IllegalArgumentException("No matched Dean to new instance!");
                    }
                    break;
                }
                case VALUE: {
                    param = paramRefs[i].getValue();
                    break;
                }
                default: {
                    param = null;
                    break;
                }
            }
            parameters[i] = param;
        }

        try {
            return constructor.newInstance(parameters);
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
