package com.thoughtworks.kunwu;

import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.kunwu.utils.RuntimeAssert.fail;

public class CoreDeanContainer extends DeanContainer {
    private final Object mutex = new Object();
    private Map<String, DeanDefinition> deanIdDefinitionMap = new HashMap<String, DeanDefinition>();
    private Map<String, Object> singletonDeanInstanceMap = new HashMap<String, Object>();

    @Override
    public Object getDeanInstance(String id) {
        DeanDefinition deanDefinition = deanIdDefinitionMap.get(id);
        if (deanDefinition == null) {
            throw new IllegalArgumentException("No Dean defined for given id: " + id);
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
                        deanInstance = buildInstance(deanDefinition);
                        singletonDeanInstanceMap.put(deanId, deanInstance);
                    }
                }
                break;
            }
            case PROTOTYPE: {
                deanInstance = buildInstance(deanDefinition);
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

    private Object buildInstance(DeanDefinition deanDefinition) {
        DeanInstanceBuilder deanInstanceBuilder = new DeanInstanceBuilder(this, deanDefinition);
        return deanInstanceBuilder.buildInstance();
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
    public DeanDefinition getDeanDefinition(String id) {
        if (!deanIdDefinitionMap.containsKey(id)) {
            throw new IllegalArgumentException("No Dean of id: " + id);
        }

        return deanIdDefinitionMap.get(id);
    }
}
