package com.thoughtworks.kunwu.context;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DefineDean;
import com.thoughtworks.kunwu.container.CoreDeanContainer;
import com.thoughtworks.kunwu.container.DeanContainer;
import com.thoughtworks.kunwu.dean.DeanDefinition;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class PackageBasedDeanContext implements DeanContext {
    private final Set<String> configPackages;
    private final DeanContainer delegateContainer;

    public PackageBasedDeanContext(Set<String> configPackages) {
        this(configPackages, new CoreDeanContainer());
    }

    public PackageBasedDeanContext(Set<String> configPackages, DeanContainer delegateContainer) {
        this.configPackages = configPackages;
        this.delegateContainer = delegateContainer;
    }

    public void scanAll() throws IOException {
        ClassPath classPath = ClassPath.from(getClass().getClassLoader());
        for (String pkg : configPackages) {
            ImmutableSet<ClassPath.ClassInfo> topLevelClasses = classPath.getTopLevelClassesRecursive(pkg);
            for (ClassPath.ClassInfo topLevelClass : topLevelClasses) {
                Class<?> targetClass = topLevelClass.load();
                DeanConfig configAnnotation = targetClass.getAnnotation(DeanConfig.class);
                if (configAnnotation != null) {
                    scanClass(targetClass);
                }
            }
        }
    }

    private void scanClass(Class<?> configClass) {
        // TODO: DeanConfig object may need Dean injection as well
        Object configClassObj;
        try {
            configClassObj = configClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException("Failed to instantiate DeanConfig class", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to instantiate DeanConfig class", e);
        }

        Method[] methods = configClass.getMethods();
        for (Method method : methods) {
            DefineDean defineDeanAnnotation = method.getAnnotation(DefineDean.class);
            if (defineDeanAnnotation != null) {
                if (!DeanDefinition.class.isAssignableFrom(method.getReturnType())) {
                    throw new IllegalStateException("@DefineDean method must return a DeanDefinition: " + method.toString());
                }

                DeanDefinition deanDefinition;
                try {
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
