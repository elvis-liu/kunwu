package com.thoughtworks.kunwu.context.interdependent;

import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.DeanInject;
import com.thoughtworks.kunwu.annotation.ReturnDean;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigA {
    @DeanInject
    @DeanIdRef("stringDeanC")
    private String stringValue;

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @ReturnDean("intDean")
    public int createIntDean() {
        return 13;
    }
}
