package com.thoughtworks.kunwu.utils;

public class RuntimeAssert {
    public static void fail(String msg) {
        throw new AssertionError(msg);
    }
}
