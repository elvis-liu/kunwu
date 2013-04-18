package com.thoughtworks.kunwu;

import org.junit.Before;
import org.junit.Test;

import static com.thoughtworks.kunwu.reference.DeanReference.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ConstructorInjectTest {

    private DeanContainer deanContainer;

    @Before
    public void setUp() throws Exception {
        deanContainer = new CoreDeanContainer();
    }

    @Test
    public void shouldInjectWhenRefByValue() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(3));
        String deanId = deanContainer.addDeanDefinition(deanDefinition);

        assertThat(deanContainer.getDeanInstance(deanId, Integer.class), is(3));
    }

    @Test
    public void shouldInjectWhenRefByClass() throws Exception {
        // given
        DeanDefinition intDeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(3));
        deanContainer.addDeanDefinition(intDeanDefinition);

        DeanDefinition testDeanDefinition = new DeanDefinition(BasicTestClass.class).constructBy(refByClass(Integer.class));
        String testDeanId = deanContainer.addDeanDefinition(testDeanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(testDeanId, BasicTestClass.class);

        // then
        assertThat(testObj.getIntegerValue(), is(3));
    }

    @Test
    public void shouldInjectWhenRefByPrimitiveTypeForWrapperDean() throws Exception {
        // given
        DeanDefinition intDeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(3)).id("int");
        deanContainer.addDeanDefinition(intDeanDefinition);

        DeanDefinition testDeanDefinition = new DeanDefinition(BasicTestClass.class).constructBy(refByClass(int.class));
        String testDeanId = deanContainer.addDeanDefinition(testDeanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(testDeanId, BasicTestClass.class);

        // then
        assertThat(testObj.getIntegerValue(), nullValue());
        assertThat(testObj.getIntValue(), is(3));
    }

    @Test
    public void shouldInjectWhenRefById() throws Exception {
        // given
        DeanDefinition intDeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(3)).id("intDean");
        deanContainer.addDeanDefinition(intDeanDefinition);

        DeanDefinition testDeanDefinition = new DeanDefinition(BasicTestClass.class).constructBy(refById("intDean"));
        String testDeanId = deanContainer.addDeanDefinition(testDeanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(testDeanId, BasicTestClass.class);

        // then
        assertThat(testObj.getIntegerValue(), is(3));
        assertThat(testObj.getIntValue(), is(0));
    }

    @Test
    public void shouldInjectWhenRefByIdFromAssignableTypeDean() throws Exception {
        // given
        DeanDefinition intDeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(3)).id("intDean");
        deanContainer.addDeanDefinition(intDeanDefinition);

        DeanDefinition testDeanDefinition = new DeanDefinition(ConstructorWithOnlyNumberParamClass.class)
                .constructBy(refById("intDean"));
        String testDeanId = deanContainer.addDeanDefinition(testDeanDefinition);

        // when
        ConstructorWithOnlyNumberParamClass testObj = deanContainer.getDeanInstance(testDeanId, ConstructorWithOnlyNumberParamClass.class);

        // then
        assertThat(testObj.getNumberValue().intValue(), is(3));
    }

    @Test
    public void shouldInjectWhenConstructorHasMultiParams() throws Exception {
        // given
        DeanDefinition stringDeanDefinition = new DeanDefinition(String.class).constructBy(refByValue("test"));
        deanContainer.addDeanDefinition(stringDeanDefinition);

        DeanDefinition testDeanDefinition = new DeanDefinition(BasicTestClass.class).constructBy(refByClass(String.class), refByValue(3));
        String testDeanId = deanContainer.addDeanDefinition(testDeanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(testDeanId, BasicTestClass.class);

        // then
        assertThat(testObj.getIntegerValue(), nullValue());
        assertThat(testObj.getIntValue(), is(3));
        assertThat(testObj.getStringValue(), is("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoMatchedConstructor() throws Exception {
        // given
        DeanDefinition testDeanDefinition = new DeanDefinition(BasicTestClass.class)
                .constructBy(refByClass(String.class), refByClass(String.class));
        String testDeanId = deanContainer.addDeanDefinition(testDeanDefinition);

        // when
        deanContainer.getDeanInstance(testDeanId, BasicTestClass.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoMatchedDeanForConstructor() throws Exception {
        DeanDefinition testDeanDefinition = new DeanDefinition(BasicTestClass.class)
                .constructBy(refByClass(Integer.class));
        String testDeanId = deanContainer.addDeanDefinition(testDeanDefinition);

        // when
        deanContainer.getDeanInstance(testDeanId, BasicTestClass.class);
    }

    @Test
    public void shouldUseDefaultConstructorWithoutConstructorSpecified() throws Exception {
        // given
        DeanDefinition testDeanDefinition = new DeanDefinition(BasicTestClass.class);
        String testDeanId = deanContainer.addDeanDefinition(testDeanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(testDeanId, BasicTestClass.class);

        // then
        assertThat(testObj.getStringValue(), is("defaultConstructor"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInjectWhenRefByClassEvenHaveAssignableDeans() throws Exception {
        // given
        DeanDefinition intDeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(3));
        deanContainer.addDeanDefinition(intDeanDefinition);

        DeanDefinition testDeanDefinition = new DeanDefinition(ConstructorWithOnlyNumberParamClass.class)
                .constructBy(refByClass(Number.class));
        String testDeanId = deanContainer.addDeanDefinition(testDeanDefinition);

        // when
        deanContainer.getDeanInstance(testDeanId, ConstructorWithOnlyNumberParamClass.class);
    }

    @Test
    public void shouldInjectWithMixedRefs() throws Exception {
        // given
        DeanDefinition intDeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(3)).id("intDean");
        deanContainer.addDeanDefinition(intDeanDefinition);

        DeanDefinition booleanDeanDefinition = new DeanDefinition(Boolean.class).constructBy(refByValue(true));
        deanContainer.addDeanDefinition(booleanDeanDefinition);

        DeanDefinition testDeanDefinition = new DeanDefinition(ClassWithMixedValueConstructor.class)
                .constructBy(refByValue((short) 12), refByValue(13l), refByClass(boolean.class), refByValue((byte) 14), refByValue(15.0), refByValue((float) 16.0), refById("intDean"));
        String testDeanId = deanContainer.addDeanDefinition(testDeanDefinition);

        // when
        ClassWithMixedValueConstructor testObj = deanContainer.getDeanInstance(testDeanId, ClassWithMixedValueConstructor.class);

        // then
        assertThat(testObj.getShortValue(), is((short)12));
        assertThat(testObj.getLongValue(), is(13l));
        assertThat(testObj.getByteValue(), is((byte)14));
        assertThat(testObj.getDoubleValue(), is(15.0));
        assertThat(testObj.getFloatValue(), is((float)16.0));
        assertThat(testObj.getIntValue(), is(3));
        assertThat(testObj.isBooleanValue(), is(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInjectWithInvalidIdRef() throws Exception {
        DeanDefinition testDeanDefinition = new DeanDefinition(BasicTestClass.class)
                .constructBy(refById("notExisted"));
        String testDeanId = deanContainer.addDeanDefinition(testDeanDefinition);

        // when
        deanContainer.getDeanInstance(testDeanId);
    }

    public static class ClassWithMixedValueConstructor {
        private short shortValue;
        private long longValue;
        private boolean booleanValue;
        private byte byteValue;
        private double doubleValue;
        private float floatValue;
        private int intValue;

        public ClassWithMixedValueConstructor(short shortValue, long longValue, boolean booleanValue, byte byteValue, double doubleValue, float floatValue, Integer intValue) {
            this.shortValue = shortValue;
            this.longValue = longValue;
            this.booleanValue = booleanValue;
            this.byteValue = byteValue;
            this.doubleValue = doubleValue;
            this.floatValue = floatValue;
            this.intValue = intValue;
        }

        public short getShortValue() {
            return shortValue;
        }

        public long getLongValue() {
            return longValue;
        }

        public byte getByteValue() {
            return byteValue;
        }

        public double getDoubleValue() {
            return doubleValue;
        }

        public float getFloatValue() {
            return floatValue;
        }

        public boolean isBooleanValue() {
            return booleanValue;
        }

        public int getIntValue() {
            return intValue;
        }
    }

    public static class BasicTestClass {
        private int intValue;
        private Integer integerValue;
        private String stringValue;

        @SuppressWarnings("unused")
        public BasicTestClass() {
            stringValue = "defaultConstructor";
        }

        @SuppressWarnings("unused")
        public BasicTestClass(int intValue) {
            this.intValue = intValue;
        }

        @SuppressWarnings("unused")
        public BasicTestClass(Integer integerValue) {
            this.integerValue = integerValue;
        }

        @SuppressWarnings("unused")
        public BasicTestClass(String stringValue, int intValue) {
            this.stringValue = stringValue;
            this.intValue = intValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public Integer getIntegerValue() {
            return integerValue;
        }

        public String getStringValue() {
            return stringValue;
        }
    }

    public static class ConstructorWithOnlyNumberParamClass {
        private Number numberValue;

        public ConstructorWithOnlyNumberParamClass(Number numberValue) {
            this.numberValue = numberValue;
        }

        public Number getNumberValue() {
            return numberValue;
        }
    }
}
