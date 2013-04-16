package com.thoughtworks.kunwu.reference;

import static com.thoughtworks.kunwu.reference.DeanReferenceType.CLASS;
import static com.thoughtworks.kunwu.reference.DeanReferenceType.ID;
import static com.thoughtworks.kunwu.reference.DeanReferenceType.VALUE;

public class DeanReference {
    private final DeanReferenceType refType;
    private Class<?> classType;
    private String id;
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

    public DeanReference(String id) {
        this.id = id;
        this.refType = ID;
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

    public String getId() {
        return id;
    }

    public static DeanReference refByClass(Class<?> classType) {
        return new DeanReference(classType);
    }

    public static DeanReference refByValue(Object value) {
        return new DeanReference(value, value.getClass());
    }

    public static DeanReference refByValue(int value) {
        return new DeanReference(value, int.class);
    }

    public static DeanReference refByValue(short value) {
        return new DeanReference(value, short.class);
    }

    public static DeanReference refByValue(long value) {
        return new DeanReference(value, long.class);
    }

    public static DeanReference refByValue(byte value) {
        return new DeanReference(value, byte.class);
    }

    public static DeanReference refByValue(float value) {
        return new DeanReference(value, float.class);
    }

    public static DeanReference refByValue(double value) {
        return new DeanReference(value, double.class);
    }

    public static DeanReference refByValue(boolean value) {
        return new DeanReference(value, boolean.class);
    }

    public static DeanReference refByValue(char value) {
        return new DeanReference(value, char.class);
    }

    public static DeanReference refById(String id) {
        return new DeanReference(id);
    }
}
