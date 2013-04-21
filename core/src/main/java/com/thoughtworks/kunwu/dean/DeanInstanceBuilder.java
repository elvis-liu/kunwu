package com.thoughtworks.kunwu.dean;

import com.thoughtworks.kunwu.context.DeanContext;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.thoughtworks.kunwu.dean.DeanDefinition.getDeanDefaultName;
import static com.thoughtworks.kunwu.dean.DeanReferenceType.ID;
import static com.thoughtworks.kunwu.utils.RuntimeAssert.fail;

public class DeanInstanceBuilder {
    private final DeanContext deanContext;

    public DeanInstanceBuilder(DeanContext deanContext) {
        this.deanContext = deanContext;
    }

    public Object buildInstance(DeanDefinition deanDefinition) {
        Constructor<?> constructor = deanDefinition.getConstructor();
        DeanReference[] constructorParamRefs = deanDefinition.getConstructorParamRefs();

        if (constructor == null) {
            constructor = guessConstructor(deanDefinition.getTargetClass(), constructorParamRefs);
        }

        Object createdObj = createNewInstance(constructor, constructorParamRefs);
        injectProperties(createdObj, deanDefinition.getPropertyRefMap());

        return createdObj;
    }

    private void injectProperties(Object targetObj, Map<String, DeanReference> propertyRefs) {
        for (String propertyName : propertyRefs.keySet()) {
            DeanReference ref = propertyRefs.get(propertyName);
            try {
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(propertyName, targetObj.getClass());
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

    private Constructor<?> guessConstructor(Class<?> targetClass, DeanReference[] constructorParamRefs) {
        Set<Constructor<?>> matchedConstructors = findMatchedConstructors(targetClass, constructorParamRefs);
        if (matchedConstructors.size() > 1) {
            throw new IllegalArgumentException("Cannot determine which constructor to use!");
        }

        if (matchedConstructors.isEmpty()) {
            throw new IllegalArgumentException("Cannot find matched constructor!");
        }

        return matchedConstructors.iterator().next();
    }

    private Set<Constructor<?>> findMatchedConstructors(Class<?> targetClass, DeanReference[] paramRefs) {
        Constructor<?>[] allConstructors = targetClass.getConstructors();
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
                Object dean = deanContext.getDeanInstance(ref.getId());
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
                refObj = deanContext.getDeanInstance(getDeanDefaultName(ref.getClassType()));
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
                refObj = deanContext.getDeanInstance(ref.getId());
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
