package com.thoughtworks.kunwu;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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
        DeanContainer deanContainer = new DeanContainer();
        TestDeanA dean = new TestDeanA();
        dean.setValue(100);
        deanContainer.addDean(dean);

        // when
        ClassWithSingleConstructorSingleParam testPOJO = deanContainer.create(ClassWithSingleConstructorSingleParam.class, TestDeanA.class);

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
        ClassWithSingleConstructorMultiParams testPOJO = deanContainer.create(ClassWithSingleConstructorMultiParams.class, TestDeanA.class, TestDeanB.class);

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
        ClassWithMultiConstructorsSingleParam testPOJO = deanContainer.create(ClassWithMultiConstructorsSingleParam.class, TestDeanB.class);

        // then
        assertThat(testPOJO.getDeanA(), nullValue());
        assertThat(testPOJO.getDeanB().getValue(), is("test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoMatchedConstructor() throws Exception {
        deanContainer.create(ClassWithMultiConstructorsSingleParam.class, String.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoMatchedDeanForConstructor() throws Exception {
        // given
        TestDeanA deanA = new TestDeanA();
        deanA.setValue(50);
        deanContainer.addDean(deanA);

        // when
        deanContainer.create(ClassWithMultiConstructorsSingleParam.class, TestDeanB.class);
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
        ClassWithMultiConstructorsSameTypeParams testPOJO = deanContainer.create(ClassWithMultiConstructorsSameTypeParams.class, TestDeanB.class, TestDeanA.class);

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
        deanContainer.create(ClassWithSingleConstructorSingleParam.class, TestDeanA.class);
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
}
