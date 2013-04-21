package com.thoughtworks.kunwu.context.simple_configs;

import com.thoughtworks.kunwu.annotation.DefineDean;
import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.dean.DeanDefinition;

import static com.thoughtworks.kunwu.dean.DeanDefinition.defineDean;
import static com.thoughtworks.kunwu.dean.DeanReference.refByValue;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigB {
    @DefineDean("testDeanB")
    public DeanDefinition defineTestDeanB() {
        return defineDean(Integer.class).constructorParams(refByValue(13));
    }
}
