package com.thoughtworks.kunwu.dean;

import com.thoughtworks.kunwu.context.DeanContext;
import com.thoughtworks.kunwu.exception.NoSuchDeanException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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

    public Object buildInstance(DeanDefinition deanDefinition) throws NoSuchDeanException {
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
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(targetObj.getClass(), Introspector.USE_ALL_BEANINFO);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            Map<String, PropertyDescriptor> propertyNameDescriptorMap = new HashMap<String, PropertyDescriptor>(propertyDescriptors.length);
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                propertyNameDescriptorMap.put(descriptor.getName(), descriptor);
            }

            for (String propertyName : propertyRefs.keySet()) {
                DeanReference ref = propertyRefs.get(propertyName);
                PropertyDescriptor descriptor = propertyNameDescriptorMap.get(propertyName);
                if (descriptor == null) {
                    throw new IllegalArgumentException("Failed to find property: " + propertyName + " within: " + targetObj.getClass().getName());
                }
                Method writeMethod = descriptor.getWriteMethod();
                if (writeMethod == null) {
                    throw new IllegalArgumentException("No setter method for property: " + propertyName + " within: " + targetObj.getClass().getName());
                }
                try {
                    writeMethod.invoke(targetObj, getRefObject(ref));
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                } catch (InvocationTargetException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        } catch (IntrospectionException e) {
            throw new IllegalArgumentException("Failed to inject property for class: " + targetObj.getClass().getName());
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
