package com.thoughtworks.kunwu;

import com.google.common.base.Predicate;
import com.thoughtworks.kunwu.reference.DeanReference;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static com.thoughtworks.kunwu.utils.CollectionUtils.arrayFilter;

public class DeanBuilder <T> {
    private final DeanContainer deanContainer;
    private final Class<T> targetClass;
    private DeanReference[] constructorParamRefs;

    DeanBuilder(DeanContainer deanContainer, Class<T> targetClass) {
        this.deanContainer = deanContainer;
        this.targetClass = targetClass;
    }

    public DeanBuilder<T> constructBy(DeanReference... paramRefs) {
        constructorParamRefs = paramRefs;
        return this;
    }

    public T create() {
        if (deanContainer.getDean(targetClass) != null) {
            return targetClass.cast(deanContainer.getDean(targetClass));
        }

        if (constructorParamRefs == null) {
            constructorParamRefs = new DeanReference[0];
        }

        Constructor<T>[] matchedConstructors = findMatchedConstructors(targetClass, constructorParamRefs);
        if (matchedConstructors.length > 1) {
            throw new IllegalArgumentException("Cannot determine which constructor to use!");
        }

        if (matchedConstructors.length == 0) {
            throw new IllegalArgumentException("Cannot find matched constructor!");
        }

        T createdObj = createNewInstance(matchedConstructors[0], constructorParamRefs);

        deanContainer.addDean(createdObj);

        return createdObj;
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
                    param = deanContainer.getDean(paramRefs[i].getClassType());
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
}
