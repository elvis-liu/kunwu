package com.thoughtworks.kunwu.context.interdependent;

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

    @ReturnDean("stringDeanB")
    public String createStringDeanB() {
        return "stringB";
    }
}
