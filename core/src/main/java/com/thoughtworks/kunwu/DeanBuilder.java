package com.thoughtworks.kunwu;

import com.thoughtworks.kunwu.reference.DeanReference;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import static com.thoughtworks.kunwu.utils.RuntimeAssert.fail;

public class DeanBuilder<T> {
    private final DeanContainer deanContainer;
    private final Class<T> targetClass;
    private DeanReference[] constructorParamRefs;
    private String deanId;

    DeanBuilder(DeanContainer deanContainer, Class<T> targetClass) {
        this.deanContainer = deanContainer;
        this.targetClass = targetClass;
    }

    public DeanBuilder<T> constructBy(DeanReference... paramRefs) {
        constructorParamRefs = paramRefs;
        return this;
    }

    public DeanBuilder<T> id(String deanId) {
        this.deanId = deanId;
        return this;
    }

    public T create() {
        T createdObj = createNewInstance(getConstructor(), constructorParamRefs);

        deanContainer.addDean(getDeanId(), createdObj);

        return createdObj;
    }

    private String getDeanId() {
        if (deanId == null) {
            return targetClass.getSimpleName();
        } else {
            return deanId;
        }
    }

    private Constructor<T> getConstructor() {
        Set<Constructor<T>> matchedConstructors = findMatchedConstructors(targetClass, constructorParamRefs);
        if (matchedConstructors.size() > 1) {
            throw new IllegalArgumentException("Cannot determine which constructor to use!");
        }

        if (matchedConstructors.isEmpty()) {
            throw new IllegalArgumentException("Cannot find matched constructor!");
        }

        return matchedConstructors.iterator().next();
    }

    private <T> Set<Constructor<T>> findMatchedConstructors(Class<T> targetClass, DeanReference[] paramRefs) {
        Constructor<T>[] allConstructors = (Constructor<T>[]) targetClass.getConstructors();
        if (paramRefs == null) {
            paramRefs = new DeanReference[0];
        }
        Set<Constructor<T>> matchedConstructors = new HashSet<Constructor<T>>();

        for (Constructor<T> constructor : allConstructors) {
            Class<?>[] constructorParamTypes = constructor.getParameterTypes();
            if (isParamRefsMatchWithTypes(paramRefs, constructorParamTypes)) {
                matchedConstructors.add(constructor);
            }
        }

        return matchedConstructors;
    }

    private boolean isParamRefsMatchWithTypes(DeanReference[] paramRefs, Class<?>[] paramTypes) {
        if (paramRefs.length != paramTypes.length) {
            return false;
        }

        for (int i = 0; i < paramRefs.length; i++) {
            switch (paramRefs[i].getRefType()) {
                case CLASS:
                case VALUE: {
                    if (!paramRefs[i].getClassType().equals(paramTypes[i])) {
                        return false;
                    }
                    break;
                }
                case ID: {
                    Object dean = deanContainer.getDean(paramRefs[i].getId());
                    if (dean == null) {
                        throw new IllegalArgumentException("No Dean with Id: " + paramRefs[i].getId());
                    }
                    if (!paramTypes[i].isAssignableFrom(dean.getClass())) {
                        return false;
                    }
                    break;
                }
                default: {
                    fail("Unknown ref type: " + paramRefs[i].getRefType());
                }
            }
        }

        return true;
    }

    private <T> T createNewInstance(Constructor<T> constructor, DeanReference[] paramRefs) {
        Object[] parameters = getReferencedDeans(paramRefs);

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

    private Object[] getReferencedDeans(DeanReference[] paramRefs) {
        if (paramRefs == null || paramRefs.length == 0) {
            return new Object[0];
        }

        Object[] parameters = new Object[paramRefs.length];
        for (int i = 0; i < paramRefs.length; i++) {
            Object param;
            switch (paramRefs[i].getRefType()) {
                case CLASS: {
                    param = deanContainer.getDean(paramRefs[i].getClassType().getSimpleName());
                    if (param == null) {
                        throw new IllegalArgumentException("No Dean of type: " + paramRefs[i].getClassType().getName());
                    }
                    break;
                }
                case VALUE: {
                    param = paramRefs[i].getValue();
                    break;
                }
                case ID: {
                    param = deanContainer.getDean(paramRefs[i].getId());
                    if (param == null) {
                        throw new IllegalArgumentException("No Dean with Id: " + paramRefs[i].getId());
                    }
                    break;
                }
                default: {
                    fail("Unknown ref type: " + paramRefs[i].getRefType());
                    param = null;
                    break;
                }
            }
            parameters[i] = param;
        }
        return parameters;
    }
}
