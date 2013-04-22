package com.thoughtworks.kunwu.context;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DefineDean;
import com.thoughtworks.kunwu.annotation.ReturnDean;
import com.thoughtworks.kunwu.container.CoreDeanContainer;
import com.thoughtworks.kunwu.container.DeanContainer;
import com.thoughtworks.kunwu.dean.DeanDefinition;
import com.thoughtworks.kunwu.dean.DeanReference;
import com.thoughtworks.kunwu.exception.NoSuchDeanException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static com.thoughtworks.kunwu.dean.DeanDefinition.defineDeanByAnnotation;
import static com.thoughtworks.kunwu.dean.DeanReferenceReflectionUtil.getMethodParamReferences;

public class PackageBasedDeanContext implements DeanContext {
    private final Set<String> configPackages;
    private final DeanContainer delegateContainer;
    private final ContextDeanInstanceBuilder instanceBuilder;
    private final ContextDeanReferenceResolver referenceResolver;

    public PackageBasedDeanContext(Set<String> configPackages) {
        this(configPackages, new CoreDeanContainer());
    }

    public PackageBasedDeanContext(Set<String> configPackages, DeanContainer delegateContainer) {
        this.configPackages = configPackages;
        this.delegateContainer = delegateContainer;
        this.instanceBuilder = new ContextDeanInstanceBuilder(delegateContainer);
        this.referenceResolver = new ContextDeanReferenceResolver(delegateContainer);
    }

    public void scanAll() throws IOException {
        Set<Class<?>> classesToScan = findAllTheConfigClasses();
        Map<Method, Object> failedConfigMethods = new HashMap<Method, Object>();
        int lastRoundRemainingClassCount = -1;
        int lastRoundRemainingMethodCount = -1;

        while (!classesToScan.isEmpty() || !failedConfigMethods.isEmpty()) {
            Iterator<Class<?>> classIterator = classesToScan.iterator();
            while (classIterator.hasNext()) {
                Class<?> targetClass = classIterator.next();
                Object configClassObj = buildConfigInstance(targetClass);
                if (configClassObj != null) {
                    classIterator.remove();
                    Method[] methods = targetClass.getMethods();
                    for (Method method : methods) {
                        if (!processConfigMethod(method, configClassObj)) {
                            failedConfigMethods.put(method, configClassObj);
                        }
                    }
                }
            }

            Iterator<Method> methodIterator = failedConfigMethods.keySet().iterator();
            while (methodIterator.hasNext()) {
                Method method = methodIterator.next();
                Object configObj = failedConfigMethods.get(method);
                if (processConfigMethod(method, configObj)) {
                    methodIterator.remove();
                }
            }

            int remainingClassesCount = classesToScan.size();
            int remainingMethodCount = failedConfigMethods.size();
            if (remainingClassesCount == lastRoundRemainingClassCount && remainingMethodCount == lastRoundRemainingMethodCount) {
                throw new IllegalStateException("Failed to inject some config classes, possibly interdependent in circle?\n\nClasses:\n"
                        + classesToScan.toString() + "\n\nMethods:\n" + failedConfigMethods.toString());
            }
            lastRoundRemainingClassCount = remainingClassesCount;
            lastRoundRemainingMethodCount = remainingMethodCount;
        }
    }

    private boolean processConfigMethod(Method method, Object configObj) {
        try {
            processDefineDeanAnnotation(method, configObj);
            processReturnDeanAnnotation(method, configObj);
            return true;
        } catch (NoSuchDeanException e) {
            return false;
        }
    }

    private Object buildConfigInstance(Class<?> targetConfigClass) {
        try {
            DeanDefinition configClassDeanDefinition = defineDeanByAnnotation(targetConfigClass);
            return instanceBuilder.buildInstance(configClassDeanDefinition);
        } catch (NoSuchDeanException e) {
            return null;
        }
    }

    private Set<Class<?>> findAllTheConfigClasses() throws IOException {
        Set<Class<?>> classesToScan = new HashSet<Class<?>>();
        ClassPath classPath = ClassPath.from(getClass().getClassLoader());
        for (String pkg : configPackages) {
            ImmutableSet<ClassPath.ClassInfo> topLevelClasses = classPath.getTopLevelClassesRecursive(pkg);
            for (ClassPath.ClassInfo topLevelClass : topLevelClasses) {
                Class<?> targetClass = topLevelClass.load();
                DeanConfig configAnnotation = targetClass.getAnnotation(DeanConfig.class);
                if (configAnnotation != null) {
                    classesToScan.add(targetClass);
                }
            }
        }
        return classesToScan;
    }

    private void processReturnDeanAnnotation(Method method, Object configClassObj) {
        ReturnDean returnDeanAnnotation = method.getAnnotation(ReturnDean.class);
        if (returnDeanAnnotation != null) {
            if (method.getReturnType().equals(void.class)) {
                throw new IllegalStateException("@ReturnDean method must not return void: " + method.toString());
            }

            DeanReference[] paramReferences = getMethodParamReferences(method);
            Object[] paramObjects = referenceResolver.getAllRefObjects(paramReferences);
            Object deanObj;
            try {
                deanObj = method.invoke(configClassObj, paramObjects);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to call config method: " + method.toString(), e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Failed to call config method: " + method.toString(), e);
            }
            if (deanObj == null) {
                throw new IllegalStateException("@ReturnDean method returned null: " + method.toString());
            }

            delegateContainer.addDeanInstance(returnDeanAnnotation.value(), deanObj);
        }
    }

    private void processDefineDeanAnnotation(Method method, Object configClassObj) {
        DefineDean defineDeanAnnotation = method.getAnnotation(DefineDean.class);
        if (defineDeanAnnotation != null) {
            if (!DeanDefinition.class.isAssignableFrom(method.getReturnType())) {
                throw new IllegalStateException("@DefineDean method must return a DeanDefinition: " + method.toString());
            }

            DeanReference[] paramReferences = getMethodParamReferences(method);
            Object[] paramObjects = referenceResolver.getAllRefObjects(paramReferences);
            DeanDefinition deanDefinition;
            try {
                deanDefinition = DeanDefinition.class.cast(method.invoke(configClassObj, paramObjects));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to call config method: " + method.toString(), e);
            } catch (InvocationTargetException e) {
                throw new IllegalStateException("Failed to call config method: " + method.toString(), e);
            }
            deanDefinition.id(defineDeanAnnotation.value());
            delegateContainer.addDeanDefinition(deanDefinition);
        }
    }

    @Override
    public Object getDeanInstance(String id) {
        return delegateContainer.getDeanInstance(id);
    }

    @Override
    public <T> T getDeanInstance(String id, Class<T> type) {
        return delegateContainer.getDeanInstance(id, type);
    }

    @Override
    public DeanDefinition getDeanDefinition(String id) {
        return delegateContainer.getDeanDefinition(id);
    }
}
