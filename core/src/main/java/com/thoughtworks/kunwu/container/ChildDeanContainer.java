package com.thoughtworks.kunwu.container;

import com.thoughtworks.kunwu.context.DeanContext;
import com.thoughtworks.kunwu.dean.DeanDefinition;

public class ChildDeanContainer extends DeanContainer {
    private final DeanContext parentContainer;
    private final DeanContainer delegateContainer;

    public ChildDeanContainer(DeanContext parentContainer) {
        this.parentContainer = parentContainer;
        this.delegateContainer = new CoreDeanContainer();
    }

    @Override
    public Object getDeanInstance(String id) {
        DeanDefinition deanDefinition = delegateContainer.getDeanDefinition(id);
        if (deanDefinition == null) {
            return parentContainer.getDeanInstance(id);
        } else {
            return delegateContainer.getDeanInstance(id);
        }
    }

    @Override
    public String addDeanDefinition(DeanDefinition deanDefinition) {
        return delegateContainer.addDeanDefinition(deanDefinition);
    }

    @Override
    public void addDeanInstance(String id, Object deanInstance) {
        delegateContainer.addDeanInstance(id, deanInstance);
    }

    @Override
    public DeanDefinition getDeanDefinition(String id) {
        DeanDefinition deanDefinition = delegateContainer.getDeanDefinition(id);
        if (deanDefinition == null) {
            deanDefinition = parentContainer.getDeanDefinition(id);
        }
        return deanDefinition;
    }
}