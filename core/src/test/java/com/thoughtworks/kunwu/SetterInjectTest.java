package com.thoughtworks.kunwu;

import org.junit.Before;
import org.junit.Test;

import static com.thoughtworks.kunwu.reference.DeanReference.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SetterInjectTest {

    private DeanContainer deanContainer;

    @Before
    public void setUp() throws Exception {
        deanContainer = new BasicDeanContainer();
    }

    @Test
    public void shouldInjectOnValueRef() throws Exception {
        // given
        DeanDefinition deanDefinition = new DeanDefinition(BasicTestClass.class).property("intValue", refByValue(3));
        String deanId = deanContainer.addDean(deanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(deanId, BasicTestClass.class);

        // then
        assertThat(testObj.getIntValue(), is(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInjectOnIllegalProperty() throws Exception {
        // given
        DeanDefinition deanDefinition = new DeanDefinition(BasicTestClass.class).property("notExisted", refByValue(3));
        String deanId = deanContainer.addDean(deanDefinition);

        // when
        deanContainer.getDeanInstance(deanId, BasicTestClass.class);
    }

    @Test
    public void shouldInjectOnIdRef() throws Exception {
        // given
        DeanDefinition integerDeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(10)).id("integerDean");
        deanContainer.addDean(integerDeanDefinition);

        DeanDefinition deanDefinition = new DeanDefinition(BasicTestClass.class).property("intValue", refById("integerDean"));
        String deanId = deanContainer.addDean(deanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(deanId, BasicTestClass.class);

        // then
        assertThat(testObj.getIntValue(), is(10));
    }

    @Test
    public void shouldInjectOnClassRef() throws Exception {
        // given
        DeanDefinition integerDeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(10));
        deanContainer.addDean(integerDeanDefinition);

        DeanDefinition deanDefinition = new DeanDefinition(BasicTestClass.class).property("intValue", refByClass(Integer.class));
        String deanId = deanContainer.addDean(deanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(deanId, BasicTestClass.class);

        // then
        assertThat(testObj.getIntValue(), is(10));
    }

    @Test
    public void shouldInjectOnParentClassProperty() throws Exception {
        // given
        DeanDefinition integerDeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(10));
        deanContainer.addDean(integerDeanDefinition);

        DeanDefinition deanDefinition = new DeanDefinition(ChildTestClass.class).property("intValue", refByClass(Integer.class));
        String deanId = deanContainer.addDean(deanDefinition);

        // when
        ChildTestClass testObj = deanContainer.getDeanInstance(deanId, ChildTestClass.class);

        // then
        assertThat(testObj.getIntValue(), is(10));
    }

    @Test
    public void shouldInjectFromAssignableDean() throws Exception {
        // given
        DeanDefinition integerDeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(10));
        deanContainer.addDean(integerDeanDefinition);

        DeanDefinition deanDefinition = new DeanDefinition(ChildTestClass.class).property("numberValue", refByClass(Integer.class));
        String deanId = deanContainer.addDean(deanDefinition);

        // when
        ChildTestClass testObj = deanContainer.getDeanInstance(deanId, ChildTestClass.class);

        // then
        assertThat(testObj.getIntValue(), is(0));
        assertThat(testObj.getNumberValue().intValue(), is(10));
    }

    @Test
    public void shouldInjectMultiProperties() throws Exception {
        // given
        DeanDefinition int1DeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(10)).id("int1");
        deanContainer.addDean(int1DeanDefinition);

        DeanDefinition int2DeanDefinition = new DeanDefinition(Integer.class).constructBy(refByValue(20)).id("int2");
        deanContainer.addDean(int2DeanDefinition);

        DeanDefinition deanDefinition = new DeanDefinition(BasicTestClass.class).property("numberValue", refById("int1")).property("intValue", refById("int2"));
        String deanId = deanContainer.addDean(deanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(deanId, BasicTestClass.class);

        // then
        assertThat(testObj.getNumberValue().intValue(), is(10));
        assertThat(testObj.getIntValue(), is(20));
    }

    public static class BasicTestClass {
        private int intValue;
        private Number numberValue;

        public int getIntValue() {
            return intValue;
        }

        @SuppressWarnings("unused")
        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public Number getNumberValue() {
            return numberValue;
        }

        @SuppressWarnings("unused")
        public void setNumberValue(Number numberValue) {
            this.numberValue = numberValue;
        }
    }

    public static class ChildTestClass extends BasicTestClass {
    }
}
