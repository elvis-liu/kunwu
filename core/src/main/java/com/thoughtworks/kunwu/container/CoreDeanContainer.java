package com.thoughtworks.kunwu.container;

import com.thoughtworks.kunwu.dean.DeanDefinition;
import com.thoughtworks.kunwu.dean.DeanInstanceBuilder;
import com.thoughtworks.kunwu.exception.NoSuchDeanException;

import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.kunwu.dean.DeanDefinition.defineDean;
import static com.thoughtworks.kunwu.dean.DeanScope.SINGLETON;
import static com.thoughtworks.kunwu.utils.RuntimeAssert.fail;

public class CoreDeanContainer extends DeanContainer {
    private final Object mutex = new Object();
    private Map<String, DeanDefinition> deanIdDefinitionMap = new HashMap<String, DeanDefinition>();
    private Map<String, Object> singletonDeanInstanceMap = new HashMap<String, Object>();
    private DeanInstanceBuilder deanInstanceBuilder = new DeanInstanceBuilder(this);

    @Override
    public Object getDeanInstance(String id) {
        DeanDefinition deanDefinition = deanIdDefinitionMap.get(id);
        if (deanDefinition == null) {
            throw new NoSuchDeanException(id);
        }

        return getInstanceByDefinition(deanDefinition);
    }

    private Object getInstanceByDefinition(DeanDefinition deanDefinition) {
        Object deanInstance;
        switch (deanDefinition.getScope()) {
            case SINGLETON: {
                synchronized (mutex) {
                    String deanId = deanDefinition.getDeanId();
                    deanInstance = singletonDeanInstanceMap.get(deanId);
                    if (deanInstance == null) {
                        deanInstance = deanInstanceBuilder.buildInstance(deanDefinition);
                        singletonDeanInstanceMap.put(deanId, deanInstance);
                    }
                }
                break;
            }
            case PROTOTYPE: {
                deanInstance = deanInstanceBuilder.buildInstance(deanDefinition);
                break;
            }
            default: {
                fail("Unknown scope type: " + deanDefinition.getScope());
                deanInstance = null;
                break;
            }
        }
        return deanInstance;
    }

    @Override
    public String addDeanDefinition(DeanDefinition deanDefinition) {
        String id = deanDefinition.getDeanId();
        synchronized (mutex) {
            if (deanIdDefinitionMap.containsKey(id)) {
                throw new IllegalArgumentException("Dean of given id already exists: " + id);
            }

            deanIdDefinitionMap.put(id, DeanDefinition.copyOf(deanDefinition));
        }
        return id;
    }

    @Override
    public void addDeanInstance(String id, Object deanInstance) {
        DeanDefinition deanDefinition = defineDean(deanInstance.getClass()).id(id).scope(SINGLETON);
        synchronized (mutex) {
            addDeanDefinition(deanDefinition);
            singletonDeanInstanceMap.put(id, deanInstance);
        }
    }

    @Override
    public DeanDefinition getDeanDefinition(String id) {
        DeanDefinition deanDefinition = deanIdDefinitionMap.get(id);
        if (deanDefinition == null) {
            throw new NoSuchDeanException(id);
        }
        return DeanDefinition.copyOf(deanDefinition);
    }
}
