package com.thoughtworks.kunwu;

import com.thoughtworks.kunwu.reference.DeanReference;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import static com.thoughtworks.kunwu.DeanDefinition.getDeanDefaultName;
import static com.thoughtworks.kunwu.reference.DeanReferenceType.ID;
import static com.thoughtworks.kunwu.utils.RuntimeAssert.fail;

public class DeanBuilder {
    private final DeanContainer deanContainer;
    private final DeanDefinition deanDefinition;

    DeanBuilder(DeanContainer deanContainer, DeanDefinition deanDefinition) {
        this.deanContainer = deanContainer;
        this.deanDefinition = deanDefinition;
    }

    public Object create() {
        Object createdObj = createNewInstance(getConstructor(), deanDefinition.getConstructorParamRefs());
        injectProperties(createdObj);

        return createdObj;
    }

    private void injectProperties(Object targetObj) {
        for (String propertyName : deanDefinition.getPropertyRefs().keySet()) {
            DeanReference ref = deanDefinition.getPropertyRefs().get(propertyName);
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, deanDefinition.getTargetClass());
                Method method = propertyDescriptor.getWriteMethod();
                method.invoke(targetObj, getRefObject(ref));
            } catch (InvocationTargetException e) {
                throw new IllegalStateException(e);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            } catch (IntrospectionException e) {
                throw new IllegalArgumentException("Cannot inject property: " + propertyName, e);
            }
        }
    }

    private Constructor<?> getConstructor() {
        Set<Constructor<?>> matchedConstructors = findMatchedConstructors(deanDefinition.getConstructorParamRefs());
        if (matchedConstructors.size() > 1) {
            throw new IllegalArgumentException("Cannot determine which constructor to use!");
        }

        if (matchedConstructors.isEmpty()) {
            throw new IllegalArgumentException("Cannot find matched constructor!");
        }

        return matchedConstructors.iterator().next();
    }

    private Set<Constructor<?>> findMatchedConstructors(DeanReference[] paramRefs) {
        Constructor<?>[] allConstructors = deanDefinition.getTargetClass().getConstructors();
        if (paramRefs == null) {
            paramRefs = new DeanReference[0];
        }
        Set<Constructor<?>> matchedConstructors = new HashSet<Constructor<?>>();

        for (Constructor<?> constructor : allConstructors) {
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
            Class<?> refClassType = getRefClassType(paramRefs[i]);
            if (paramRefs[i].getRefType() == ID) {
                if (!paramTypes[i].isAssignableFrom(refClassType)) {
                    return false;
                }
            } else {
                if (!paramTypes[i].equals(refClassType)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Class<?> getRefClassType(DeanReference ref) {
        Class<?> classType;

        switch (ref.getRefType()) {
            case CLASS:
            case VALUE: {
                classType = ref.getClassType();
                break;
            }
            case ID: {
                Object dean = deanContainer.getDeanInstance(ref.getId());
                if (dean == null) {
                    throw new IllegalArgumentException("No Dean with Id: " + ref.getId());
                }
                classType = dean.getClass();
                break;
            }
            default: {
                fail("Unknown ref type: " + ref.getRefType());
                classType = null;
                break;
            }
        }

        return classType;
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
            parameters[i] = getRefObject(paramRefs[i]);
        }
        return parameters;
    }

    private Object getRefObject(DeanReference ref) {
        Object refObj;
        switch (ref.getRefType()) {
            case CLASS: {
                refObj = deanContainer.getDeanInstance(getDeanDefaultName(ref.getClassType()));
                if (refObj == null) {
                    throw new IllegalArgumentException("No Dean of type: " + ref.getClassType().getName());
                }
                break;
            }
            case VALUE: {
                refObj = ref.getValue();
                break;
            }
            case ID: {
                refObj = deanContainer.getDeanInstance(ref.getId());
                if (refObj == null) {
                    throw new IllegalArgumentException("No Dean with Id: " + ref.getId());
                }
                break;
            }
            default: {
                fail("Unknown ref type: " + ref.getRefType());
                refObj = null;
                break;
            }
        }

        return refObj;
    }
}
