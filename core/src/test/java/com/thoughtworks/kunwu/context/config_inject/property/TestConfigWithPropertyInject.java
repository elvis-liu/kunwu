package com.thoughtworks.kunwu.context.config_inject.property;

import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.DeanInject;
import com.thoughtworks.kunwu.annotation.DefineDean;
import com.thoughtworks.kunwu.dean.DeanDefinition;

import static com.thoughtworks.kunwu.dean.DeanDefinition.defineDean;
import static com.thoughtworks.kunwu.dean.DeanReference.refByValue;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigWithPropertyInject {
    @DeanInject
    @DeanIdRef("integerDean")
    private int intValue;

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public int getIntValue() {
        return intValue;
    }

    @DefineDean("testDeanB")
    public DeanDefinition defineTestDeanB() {
        return defineDean(Integer.class).constructorParams(refByValue(intValue + 1));
    }
}
