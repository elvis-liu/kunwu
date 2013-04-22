package com.thoughtworks.kunwu.context.config_method_inject_interdependent_in_circle.config_method_inject_interdependent;

import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.ReturnDean;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigB {
    @ReturnDean("stringDeanB")
    public String createStringDeanB(@DeanIdRef("stringDeanA") String stringA) {
        return stringA + "_stringB";
    }
}
