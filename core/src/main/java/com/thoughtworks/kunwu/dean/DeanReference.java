package com.thoughtworks.kunwu.dean;

public class DeanReference {
    private final DeanReferenceType refType;
    private Class<?> classType;
    private String id;
    private Object value;

    private DeanReference(Class<?> classType) {
        this.classType = classType;
        this.refType = DeanReferenceType.CLASS;
    }

    private DeanReference(Object value, Class<?> classType) {
        this.value = value;
        this.classType = classType;
        this.refType = DeanReferenceType.VALUE;
    }

    public DeanReference(String id) {
        this.id = id;
        this.refType = DeanReferenceType.ID;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeanReference that = (DeanReference) o;

        if (classType != null ? !classType.equals(that.classType) : that.classType != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (refType != that.refType) return false;
        //noinspection RedundantIfStatement
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = refType.hashCode();
        result = 31 * result + (classType != null ? classType.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
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
