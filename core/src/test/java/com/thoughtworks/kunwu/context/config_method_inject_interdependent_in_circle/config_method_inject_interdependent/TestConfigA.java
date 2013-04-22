package com.thoughtworks.kunwu.context.config_method_inject_interdependent_in_circle.config_method_inject_interdependent;

import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.DefineDean;
import com.thoughtworks.kunwu.dean.DeanDefinition;

import static com.thoughtworks.kunwu.dean.DeanDefinition.defineDean;
import static com.thoughtworks.kunwu.dean.DeanReference.refByValue;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigA {
    @DefineDean("stringDeanA")
    public DeanDefinition defineStringDeanA(@DeanIdRef("stringDeanB") String stringB) {
        return defineDean(String.class).constructorParams(refByValue("stringA_" + stringB));
    }
}
