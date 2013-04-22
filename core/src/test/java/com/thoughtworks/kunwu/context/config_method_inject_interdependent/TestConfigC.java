package com.thoughtworks.kunwu.context.config_method_inject_interdependent;

import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.ReturnDean;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigC {
    @ReturnDean("stringDeanC")
    public String createStringDeanB(@DeanIdRef("stringDeanA") String stringA) {
        return stringA + "_stringC";
    }
}
