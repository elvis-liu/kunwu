package com.thoughtworks.kunwu.context.config_method_inject;

import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.DefineDean;
import com.thoughtworks.kunwu.annotation.ReturnDean;
import com.thoughtworks.kunwu.dean.DeanDefinition;

import static com.thoughtworks.kunwu.dean.DeanDefinition.defineDean;
import static com.thoughtworks.kunwu.dean.DeanReference.refByValue;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfig {
    @ReturnDean("stringDeanA")
    public String createStringDeanA(@DeanIdRef("baseStringDean") String baseString) {
        return baseString + "_stringA";
    }

    @DefineDean("stringDeanB")
    public DeanDefinition defineStringDeanB(@DeanIdRef("baseStringDean") String baseString) {
        return defineDean(String.class).constructorParams(refByValue(baseString + "_stringB"));
    }
}
