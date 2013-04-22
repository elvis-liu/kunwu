package com.thoughtworks.kunwu.context.config_inject_interdependent_in_circle;

import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.DeanInject;
import com.thoughtworks.kunwu.annotation.ReturnDean;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigB {
    @DeanInject
    @DeanIdRef("intDean")
    private int intValue;

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    @ReturnDean("stringDean")
    public String createStringDeanB() {
        return "stringB";
    }
}
