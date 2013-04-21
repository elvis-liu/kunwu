package com.thoughtworks.kunwu.context.config_inject.constructor;

import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.DeanInject;
import com.thoughtworks.kunwu.annotation.ReturnDean;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigWithConstructorInject {
    private final String stringValue;

    @DeanInject
    public TestConfigWithConstructorInject(@DeanIdRef("stringDean") String stringValue) {
        this.stringValue = stringValue;
    }

    @ReturnDean("testDeanA")
    public String createTestDeanA() {
        return stringValue + "_testA";
    }
}
