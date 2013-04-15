package com.thoughtworks.kunwu.reference;

import static com.thoughtworks.kunwu.reference.DeanReferenceType.CLASS;
import static com.thoughtworks.kunwu.reference.DeanReferenceType.VALUE;

public class DeanReference {
    private final DeanReferenceType refType;
    private final Class<?> classType;
    private Object value;

    private DeanReference(Class<?> classType) {
        this.classType = classType;
        this.refType = CLASS;
    }

    private DeanReference(Object value, Class<?> classType) {
        this.value = value;
        this.classType = classType;
        this.refType = VALUE;
    }

    public DeanReferenceType getRefType() {
        return refType;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public Object getValue() {
        return value;
    }

    public static DeanReference refByClass(Class<?> classType) {
        return new DeanReference(classType);
    }

    public static DeanReference refByValue(Object value) {
        return new DeanReference(value, value.getClass());
    }

    public static DeanReference refByValue(int value) {
        return new DeanReference(value, Integer.TYPE);
    }

    public static DeanReference refByValue(short value) {
        return new DeanReference(value, Short.TYPE);
    }

    public static DeanReference refByValue(long value) {
        return new DeanReference(value, Long.TYPE);
    }

    public static DeanReference refByValue(byte value) {
        return new DeanReference(value, Byte.TYPE);
    }

    public static DeanReference refByValue(float value) {
        return new DeanReference(value, Float.TYPE);
    }

    public static DeanReference refByValue(double value) {
        return new DeanReference(value, Double.TYPE);
    }
}
