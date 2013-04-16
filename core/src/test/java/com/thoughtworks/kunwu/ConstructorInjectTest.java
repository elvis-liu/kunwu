package com.thoughtworks.kunwu;

import org.junit.Before;
import org.junit.Test;

import static com.thoughtworks.kunwu.reference.DeanReference.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class ConstructorInjectTest {

    private DeanContainer deanContainer;

    @Before
    public void setUp() throws Exception {
        deanContainer = new DeanContainer();
    }

    @Test
    public void shouldInjectPOJOWithSingleConstructorSingleParam() throws Exception {
        // given
        TestDeanA dean = deanContainer.deanBuilder(TestDeanA.class).create();
        dean.setValue(100);

        // when
        ClassWithSingleConstructorSingleParam testPOJO = deanContainer.deanBuilder(ClassWithSingleConstructorSingleParam.class).constructBy(refByClass(TestDeanA.class)).create();

        // then
        assertThat(testPOJO.getDean().getValue(), is(100));
    }

    @Test
    public void shouldInjectPOJOWithSingleConstructorMultiParams() throws Exception {
        // given
        TestDeanA deanA = deanContainer.deanBuilder(TestDeanA.class).create();
        deanA.setValue(50);
        TestDeanB deanB = deanContainer.deanBuilder(TestDeanB.class).create();
        deanB.setValue("test");

        // when
        ClassWithSingleConstructorMultiParams testPOJO = deanContainer.deanBuilder(ClassWithSingleConstructorMultiParams.class).constructBy(refByClass(TestDeanA.class), refByClass(TestDeanB.class)).create();

        // then
        assertThat(testPOJO.getDeanA().getValue(), is(50));
        assertThat(testPOJO.getDeanB().getValue(), is("test"));
    }

    @Test
    public void shouldInjectPOJOWithMultiConstructorsSingleParam() throws Exception {
        // given
        TestDeanA deanA = deanContainer.deanBuilder(TestDeanA.class).create();
        deanA.setValue(50);
        TestDeanB deanB = deanContainer.deanBuilder(TestDeanB.class).create();
        deanB.setValue("test");

        // when
        ClassWithMultiConstructorsSingleParam testPOJO = deanContainer.deanBuilder(ClassWithMultiConstructorsSingleParam.class).constructBy(refByClass(TestDeanB.class)).create();

        // then
        assertThat(testPOJO.getDeanA(), nullValue());
        assertThat(testPOJO.getDeanB().getValue(), is("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoMatchedConstructor() throws Exception {
        deanContainer.deanBuilder(ClassWithMultiConstructorsSingleParam.class).constructBy(refByClass(String.class)).create();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoMatchedDeanForConstructor() throws Exception {
        // given
        TestDeanA deanA = deanContainer.deanBuilder(TestDeanA.class).create();
        deanA.setValue(50);

        // when
        deanContainer.deanBuilder(ClassWithMultiConstructorsSingleParam.class).constructBy(refByClass(TestDeanB.class)).create();
    }

    @Test
    public void shouldUseDefaultConstructor() throws Exception {
        // given
        TestDeanA deanA = deanContainer.deanBuilder(TestDeanA.class).create();
        deanA.setValue(50);

        // when
        ClassWithDefaultConstructor testPOJO = deanContainer.deanBuilder(ClassWithDefaultConstructor.class).constructBy().create();

        // then
        assertThat(testPOJO, notNullValue());
        assertThat(testPOJO.getDeanA(), nullValue());
    }

    @Test
    public void shouldInjectPOJOWithMultiConstructorSameTypeParamsDifferentOrder() throws Exception {
        // given
        TestDeanA deanA = deanContainer.deanBuilder(TestDeanA.class).create();
        deanA.setValue(50);
        TestDeanB deanB = deanContainer.deanBuilder(TestDeanB.class).create();
        deanB.setValue("test");

        // when
        ClassWithMultiConstructorsSameTypeParams testPOJO = deanContainer.deanBuilder(ClassWithMultiConstructorsSameTypeParams.class).constructBy(refByClass(TestDeanB.class), refByClass(TestDeanA.class)).create();

        // then
        assertThat(testPOJO.getBy(), is("B,A"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionEvenHaveAssignableDeanOfChildTypes() throws Exception {
        // given
        TestDeanC deanC = deanContainer.deanBuilder(TestDeanC.class).create();
        deanC.setValue(25);
        deanC.setAnotherValue("deanC");

        // when
        deanContainer.deanBuilder(ClassWithSingleConstructorSingleParam.class).constructBy(refByClass(TestDeanA.class)).create();
    }

    @Test
    public void shouldInjectPOJOWithOnlyValueRefs() throws Exception {
        // when
        ClassWithOnlyValueRefs testPOJO = deanContainer.deanBuilder(ClassWithOnlyValueRefs.class).constructBy(refByValue(1), refByValue("test")).create();

        // then
        assertThat(testPOJO.getIntValue(), is(1));
        assertThat(testPOJO.getStringValue(), is("test"));
    }

    @Test
    public void shouldInjectPOJOWithMixedValueAndClassRefs() throws Exception {
        // given
        TestDeanA deanA = deanContainer.deanBuilder(TestDeanA.class).create();
        deanA.setValue(50);

        // when
        ClassWithMixedValueAndDeanConstructor testPOJO = deanContainer.deanBuilder(ClassWithMixedValueAndDeanConstructor.class).constructBy(refByValue((short) 12), refByValue(13l), refByValue((byte) 14), refByValue(15.0), refByValue((float) 16.0), refByClass(TestDeanA.class)).create();

        // then
        assertThat(testPOJO.getShortValue(), is((short)12));
        assertThat(testPOJO.getLongValue(), is(13l));
        assertThat(testPOJO.getByteValue(), is((byte)14));
        assertThat(testPOJO.getDoubleValue(), is(15.0));
        assertThat(testPOJO.getFloatValue(), is((float)16.0));
        assertThat(testPOJO.getDeanA().getValue(), is(50));
    }

    @Test
    public void shouldInjectPOJOWithIdRef() throws Exception {
        // given
        TestDeanA deanA = deanContainer.deanBuilder(TestDeanA.class).id("deanA1").create();
        deanA.setValue(50);

        deanA = deanContainer.deanBuilder(TestDeanA.class).id("deanA2").create();
        deanA.setValue(100);

        // when
        ClassWithSingleConstructorSingleParam testPOJO = deanContainer.deanBuilder(ClassWithSingleConstructorSingleParam.class).constructBy(refById("deanA2")).create();

        // then
        assertThat(testPOJO.getDean().getValue(), is(100));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWithInvalidIdRef() throws Exception {
        // when
        deanContainer.deanBuilder(ClassWithSingleConstructorSingleParam.class).constructBy(refById("deanA")).create();
    }

    @Test
    public void shouldInjectPOJOWithDeanOfChildTypes() throws Exception {
        // given
        TestDeanA deanA = deanContainer.deanBuilder(TestDeanA.class).id("deanA").create();
        deanA.setValue(50);

        TestDeanC deanC = deanContainer.deanBuilder(TestDeanC.class).id("deanC").create();
        deanC.setValue(100);
        deanC.setAnotherValue("test");

        // when
        ClassWithSingleConstructorSingleParam testPOJO = deanContainer.deanBuilder(ClassWithSingleConstructorSingleParam.class).constructBy(refById("deanC")).create();

        // then
        assertThat(testPOJO.getDean(), instanceOf(TestDeanC.class));
        assertThat(testPOJO.getDean().getValue(), is(100));
    }

    private static class ClassWithSingleConstructorSingleParam {
        private TestDeanA dean;

        public ClassWithSingleConstructorSingleParam(TestDeanA dean) {
            this.dean = dean;
        }

        private TestDeanA getDean() {
            return dean;
        }
    }

    private static class ClassWithSingleConstructorMultiParams {
        private TestDeanA deanA;
        private TestDeanB deanB;

        public ClassWithSingleConstructorMultiParams(TestDeanA deanA, TestDeanB deanB) {
            this.deanA = deanA;
            this.deanB = deanB;
        }

        private TestDeanA getDeanA() {
            return deanA;
        }

        private TestDeanB getDeanB() {
            return deanB;
        }
    }

    public static class TestDeanA {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    public static class TestDeanB {
        private String value;

        private String getValue() {
            return value;
        }

        private void setValue(String value) {
            this.value = value;
        }
    }

    public static class ClassWithMultiConstructorsSingleParam {
        private TestDeanA deanA;
        private TestDeanB deanB;

        @SuppressWarnings("unused")
        public ClassWithMultiConstructorsSingleParam(TestDeanA deanA) {
            this.deanA = deanA;
        }

        @SuppressWarnings("unused")
        public ClassWithMultiConstructorsSingleParam(TestDeanB deanB) {
            this.deanB = deanB;
        }

        private TestDeanA getDeanA() {
            return deanA;
        }

        private TestDeanB getDeanB() {
            return deanB;
        }
    }

    public static class ClassWithDefaultConstructor {
        private TestDeanA deanA;

        @SuppressWarnings("unused")
        public ClassWithDefaultConstructor() {
        }

        @SuppressWarnings("unused")
        public ClassWithDefaultConstructor(TestDeanA deanA) {
            this.deanA = deanA;
        }

        private TestDeanA getDeanA() {
            return deanA;
        }
    }

    public static class ClassWithMultiConstructorsSameTypeParams {
        private String by;

        @SuppressWarnings("unused")
        public ClassWithMultiConstructorsSameTypeParams(TestDeanA deanA, TestDeanB deanB) {
            this.by = "A,B";
        }

        @SuppressWarnings("unused")
        public ClassWithMultiConstructorsSameTypeParams(TestDeanB deanB, TestDeanA deanA) {
            this.by = "B,A";
        }

        private String getBy() {
            return by;
        }
    }

    public static class TestDeanC extends TestDeanA {
        private String anotherValue;

        public String getAnotherValue() {
            return anotherValue;
        }

        private void setAnotherValue(String anotherValue) {
            this.anotherValue = anotherValue;
        }
    }

    public static class ClassWithOnlyValueRefs {
        private int intValue;
        private String stringValue;

        public ClassWithOnlyValueRefs(int intValue, String stringValue) {
            this.intValue = intValue;
            this.stringValue = stringValue;
        }

        private int getIntValue() {
            return intValue;
        }

        private String getStringValue() {
            return stringValue;
        }
    }

    public static class ClassWithMixedValueAndDeanConstructor {
        private short shortValue;
        private long longValue;
        private byte byteValue;
        private double doubleValue;
        private float floatValue;
        private TestDeanA deanA;

        public ClassWithMixedValueAndDeanConstructor(short shortValue, long longValue, byte byteValue, double doubleValue, float floatValue, TestDeanA deanA) {
            this.shortValue = shortValue;
            this.longValue = longValue;
            this.byteValue = byteValue;
            this.doubleValue = doubleValue;
            this.floatValue = floatValue;
            this.deanA = deanA;
        }

        private short getShortValue() {
            return shortValue;
        }

        private long getLongValue() {
            return longValue;
        }

        private byte getByteValue() {
            return byteValue;
        }

        private double getDoubleValue() {
            return doubleValue;
        }

        private float getFloatValue() {
            return floatValue;
        }

        private TestDeanA getDeanA() {
            return deanA;
        }
    }
}
