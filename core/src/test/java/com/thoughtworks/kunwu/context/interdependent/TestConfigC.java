package com.thoughtworks.kunwu.context.interdependent;

import com.thoughtworks.kunwu.annotation.DeanConfig;
import com.thoughtworks.kunwu.annotation.ReturnDean;

@SuppressWarnings("unused")
@DeanConfig
public class TestConfigC {
    @ReturnDean("stringDeanC")
    public String createStringDeanB() {
        return "stringC";
    }
}
