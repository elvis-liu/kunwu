package com.thoughtworks.kunwu.context.simple_configs.child_pkg;

import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DefineDean;
import com.thoughtworks.kunwu.dean.DeanDefinition;

import static com.thoughtworks.kunwu.dean.DeanDefinition.defineDean;
import static com.thoughtworks.kunwu.dean.DeanReference.refByValue;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigC {
    @DefineDean("testDeanC")
    public DeanDefinition defineTestDeanC() {
        return defineDean(String.class).constructorParams(refByValue("testC"));
    }
}
