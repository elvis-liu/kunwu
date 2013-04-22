package com.thoughtworks.kunwu.context;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DefineDean;
import com.thoughtworks.kunwu.annotation.ReturnDean;
import com.thoughtworks.kunwu.container.CoreDeanContainer;
import com.thoughtworks.kunwu.container.DeanContainer;
import com.thoughtworks.kunwu.dean.DeanDefinition;
import com.thoughtworks.kunwu.dean.DeanInstanceBuilder;
import com.thoughtworks.kunwu.exception.NoSuchDeanException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.thoughtworks.kunwu.dean.DeanDefinition.defineDeanByAnnotation;

public class PackageBasedDeanContext implements DeanContext {
    private final Set<String> configPackages;
    private final DeanContainer delegateContainer;
    private final DeanInstanceBuilder deanInstanceBuilder;

    public PackageBasedDeanContext(Set<String> configPackages) {
        this(configPackages, new CoreDeanContainer());
    }

    public PackageBasedDeanContext(Set<String> configPackages, DeanContainer delegateContainer) {
        this.configPackages = configPackages;
        this.delegateContainer = delegateContainer;
        this.deanInstanceBuilder = new DeanInstanceBuilder(delegateContainer);
    }

    public void scanAll() throws IOException {
        Set<Class<?>> classesToScan = findAllTheConfigClasses();
        int lastRoundRemainingCount = -1;

        while (!classesToScan.isEmpty()) {
            Iterator<Class<?>> iterator = classesToScan.iterator();
            while (iterator.hasNext()) {
                Class<?> targetClass = iterator.next();
                try {
                    scanClass(targetClass);
                    iterator.remove();
                } catch (NoSuchDeanException e) {
                    // ignore
                }
            }

            int remainingClassesCount = classesToScan.size();
            if (remainingClassesCount == lastRoundRemainingCount) {
                throw new IllegalStateException("Failed to inject some config classes, possibly interdependent in circle?" + classesToScan.toString());
            }
            lastRoundRemainingCount = remainingClassesCount;
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

    private void scanClass(Class<?> configClass) {
        DeanDefinition configClassDeanDefinition = defineDeanByAnnotation(configClass);
        // TODO: what if the deans required by this config class are defined by another config class not scanned yet?
        Object configClassObj = deanInstanceBuilder.buildInstance(configClassDeanDefinition);

        Method[] methods = configClass.getMethods();
        for (Method method : methods) {
            processDefineDeanAnnotation(method, configClassObj);
            processReturnDeanAnnotation(method, configClassObj);
        }
    }

    private void processReturnDeanAnnotation(Method method, Object configClassObj) {
        ReturnDean returnDeanAnnotation = method.getAnnotation(ReturnDean.class);
        if (returnDeanAnnotation != null) {
            if (method.getReturnType().equals(void.class)) {
                throw new IllegalStateException("@ReturnDean method must not return void: " + method.toString());
            }

            Object deanObj;
            try {
                // TODO: allow method injection
                deanObj = method.invoke(configClassObj);
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

            DeanDefinition deanDefinition;
            try {
                // TODO: allow method injection
                deanDefinition = DeanDefinition.class.cast(method.invoke(configClassObj));
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
