package com.thoughtworks.kunwu.context.config_method_inject_interdependent;

import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.DeanInject;
import com.thoughtworks.kunwu.annotation.ReturnDean;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigD {
    @DeanInject
    @DeanIdRef("stringDeanA")
    private String stringValue;

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @ReturnDean("stringDeanD")
    public String createStringDeanD() {
        return stringValue + "_stringD";
    }
}
