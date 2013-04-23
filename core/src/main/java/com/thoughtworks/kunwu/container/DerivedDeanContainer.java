package com.thoughtworks.kunwu.container;

import com.thoughtworks.kunwu.context.DeanContext;
import com.thoughtworks.kunwu.dean.DeanDefinition;
import com.thoughtworks.kunwu.exception.NoSuchDeanException;

public class DerivedDeanContainer extends DeanContainer {
    private final DeanContext parentContainer;
    private final DeanContainer delegateContainer;

    public DerivedDeanContainer(DeanContext parentContainer) {
        this.parentContainer = parentContainer;
        this.delegateContainer = new CoreDeanContainer();
    }

    @Override
    public Object getDeanInstance(String id) {
        try {
            return delegateContainer.getDeanInstance(id);
        } catch (NoSuchDeanException e) {
            return parentContainer.getDeanInstance(id);
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
        try {
            return delegateContainer.getDeanDefinition(id);
        } catch (NoSuchDeanException e) {
            return parentContainer.getDeanDefinition(id);
        }
    }
}
