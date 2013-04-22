package com.thoughtworks.kunwu.context.config_method_inject_interdependent;

import com.google.common.base.Joiner;
import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.ReturnDean;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigB {
    @ReturnDean("stringDeanB")
    public String createStringDeanB(@DeanIdRef("stringDeanC") String stringC, @DeanIdRef("stringDeanD") String stringD) {
        return Joiner.on("_").join("stringB", stringC, stringD);
    }

    @ReturnDean("intDeanB")
    public int createIntDeanB() {
        return 1;
    }
}
