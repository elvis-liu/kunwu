package com.thoughtworks.kunwu.context.config_method_inject_interdependent;

import com.thoughtworks.kunwu.annotation.*;
import com.thoughtworks.kunwu.dean.DeanDefinition;

import static com.thoughtworks.kunwu.dean.DeanDefinition.defineDean;
import static com.thoughtworks.kunwu.dean.DeanReference.refByValue;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigA {
    @ReturnDean("intDeanA")
    public int createIntDean(@DeanIdRef("intDeanB") Integer refInt) {
        return 13 + refInt;
    }

    @DefineDean("stringDeanA")
    public DeanDefinition defineStringDeanA() {
        return defineDean(String.class).constructorParams(refByValue("stringA"));
    }
}
