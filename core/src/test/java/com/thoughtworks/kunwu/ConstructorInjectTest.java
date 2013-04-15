package com.thoughtworks.kunwu;

import com.thoughtworks.kunwu.reference.DeanReference;
import org.junit.Before;
import org.junit.Test;

import static com.thoughtworks.kunwu.reference.DeanReference.refByClass;
import static com.thoughtworks.kunwu.reference.DeanReference.refByValue;
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
        TestDeanA dean = new TestDeanA();
        dean.setValue(100);
        deanContainer.addDean(dean);

        // when
        ClassWithSingleConstructorSingleParam testPOJO = deanContainer.create(ClassWithSingleConstructorSingleParam.class, refByClass(TestDeanA.class));

        // then
        assertThat(testPOJO.getDean().getValue(), is(100));
    }

    @Test
    public void shouldInjectPOJOWithSingleConstructorMultiParams() throws Exception {
        // given
        TestDeanA deanA = new TestDeanA();
        deanA.setValue(50);
        TestDeanB deanB = new TestDeanB();
        deanB.setValue("test");
        deanContainer.addDean(deanA);
        deanContainer.addDean(deanB);

        // when
        ClassWithSingleConstructorMultiParams testPOJO = deanContainer.create(ClassWithSingleConstructorMultiParams.class, refByClass(TestDeanA.class), refByClass(TestDeanB.class));

        // then
        assertThat(testPOJO.getDeanA().getValue(), is(50));
        assertThat(testPOJO.getDeanB().getValue(), is("test"));
    }

    @Test
    public void shouldInjectPOJOWithMultiConstructorsSingleParam() throws Exception {
        // given
        TestDeanA deanA = new TestDeanA();
        deanA.setValue(50);
        TestDeanB deanB = new TestDeanB();
        deanB.setValue("test");
        deanContainer.addDean(deanA);
        deanContainer.addDean(deanB);

        // when
        ClassWithMultiConstructorsSingleParam testPOJO = deanContainer.create(ClassWithMultiConstructorsSingleParam.class, refByClass(TestDeanB.class));

        // then
        assertThat(testPOJO.getDeanA(), nullValue());
        assertThat(testPOJO.getDeanB().getValue(), is("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoMatchedConstructor() throws Exception {
        deanContainer.create(ClassWithMultiConstructorsSingleParam.class, refByClass(String.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoMatchedDeanForConstructor() throws Exception {
        // given
        TestDeanA deanA = new TestDeanA();
        deanA.setValue(50);
        deanContainer.addDean(deanA);

        // when
        deanContainer.create(ClassWithMultiConstructorsSingleParam.class, refByClass(TestDeanB.class));
    }

    @Test
    public void shouldUseDefaultConstructor() throws Exception {
        // given
        TestDeanA deanA = new TestDeanA();
        deanA.setValue(50);
        deanContainer.addDean(deanA);

        // when
        ClassWithDefaultConstructor testPOJO = deanContainer.create(ClassWithDefaultConstructor.class);

        // then
        assertThat(testPOJO, notNullValue());
        assertThat(testPOJO.getDeanA(), nullValue());
    }

    @Test
    public void shouldInjectPOJOWithMultiConstructorSameTypeParamsDifferentOrder() throws Exception {
        // given
        TestDeanA deanA = new TestDeanA();
        deanA.setValue(50);
        TestDeanB deanB = new TestDeanB();
        deanB.setValue("test");
        deanContainer.addDean(deanA);
        deanContainer.addDean(deanB);

        // when
        ClassWithMultiConstructorsSameTypeParams testPOJO = deanContainer.create(ClassWithMultiConstructorsSameTypeParams.class, refByClass(TestDeanB.class), refByClass(TestDeanA.class));

        // then
        assertThat(testPOJO.getBy(), is("B,A"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionEvenHaveAssignableDeanOfChildTypes() throws Exception {
        // given
        TestDeanC deanC = new TestDeanC();
        deanC.setAnotherValue("deanC");
        deanC.setValue(25);
        deanContainer.addDean(deanC);

        // when
        deanContainer.create(ClassWithSingleConstructorSingleParam.class, refByClass(TestDeanA.class));
    }

    @Test
    public void shouldInjectPOJOWithOnlyValueRefs() throws Exception {
        // when
        ClassWithOnlyValueRefs testPOJO = deanContainer.create(ClassWithOnlyValueRefs.class, DeanReference.refByValue(1), DeanReference.refByValue("test"));

        // then
        assertThat(testPOJO.getIntValue(), is(1));
        assertThat(testPOJO.getStringValue(), is("test"));
    }

    @Test
    public void shouldInjectPOJOWithMixedValueAndClassRefs() throws Exception {
        // given
        TestDeanA deanA = new TestDeanA();
        deanA.setValue(50);
        deanContainer.addDean(deanA);

        // when
        ClassWithMixedValueAndDeanConstructor testPOJO = deanContainer.create(ClassWithMixedValueAndDeanConstructor.class, refByValue((short) 12), refByValue(13l),
                refByValue((byte) 14), refByValue(15.0), refByValue((float) 16.0), refByClass(TestDeanA.class));

        // then
        assertThat(testPOJO.getShortValue(), is((short)12));
        assertThat(testPOJO.getLongValue(), is(13l));
        assertThat(testPOJO.getByteValue(), is((byte)14));
        assertThat(testPOJO.getDoubleValue(), is(15.0));
        assertThat(testPOJO.getFloatValue(), is((float)16.0));
        assertThat(testPOJO.getDeanA().getValue(), is(50));
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

    private static class TestDeanA {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }

    private static class TestDeanB {
        private String value;

        private String getValue() {
            return value;
        }

        private void setValue(String value) {
            this.value = value;
        }
    }

    private static class ClassWithMultiConstructorsSingleParam {
        private TestDeanA deanA;
        private TestDeanB deanB;

        public ClassWithMultiConstructorsSingleParam(TestDeanA deanA) {
            this.deanA = deanA;
        }

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

    private static class ClassWithDefaultConstructor {
        private TestDeanA deanA;

        public ClassWithDefaultConstructor() {
        }

        public ClassWithDefaultConstructor(TestDeanA deanA) {
            this.deanA = deanA;
        }

        private TestDeanA getDeanA() {
            return deanA;
        }
    }

    private static class ClassWithMultiConstructorsSameTypeParams {
        private TestDeanA deanA;
        private TestDeanB deanB;
        private String by;

        public ClassWithMultiConstructorsSameTypeParams(TestDeanA deanA, TestDeanB deanB) {
            this.deanA = deanA;
            this.deanB = deanB;
            this.by = "A,B";
        }

        public ClassWithMultiConstructorsSameTypeParams(TestDeanB deanB, TestDeanA deanA) {
            this.deanA = deanA;
            this.deanB = deanB;
            this.by = "B,A";
        }

        private String getBy() {
            return by;
        }
    }

    private static class TestDeanC extends TestDeanA {
        private String anotherValue;

        public String getAnotherValue() {
            return anotherValue;
        }

        private void setAnotherValue(String anotherValue) {
            this.anotherValue = anotherValue;
        }
    }

    private static class ClassWithOnlyValueRefs {
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

    private static class ClassWithMixedValueAndDeanConstructor {
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
