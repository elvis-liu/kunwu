package com.thoughtworks.kunwu.container;

import com.thoughtworks.kunwu.dean.DeanDefinition;
import org.junit.Before;
import org.junit.Test;

import static com.thoughtworks.kunwu.dean.DeanReference.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SetterInjectTest {

    private DeanContainer deanContainer;

    @Before
    public void setUp() throws Exception {
        deanContainer = new CoreDeanContainer();
    }

    @Test
    public void shouldInjectOnValueRef() throws Exception {
        // given
        DeanDefinition deanDefinition = DeanDefinition.defineDean(BasicTestClass.class).property("intValue", refByValue(3));
        String deanId = deanContainer.addDeanDefinition(deanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(deanId, BasicTestClass.class);

        // then
        assertThat(testObj.getIntValue(), is(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInjectOnIllegalProperty() throws Exception {
        // given
        DeanDefinition deanDefinition = DeanDefinition.defineDean(BasicTestClass.class).property("notExisted", refByValue(3));
        String deanId = deanContainer.addDeanDefinition(deanDefinition);

        // when
        deanContainer.getDeanInstance(deanId, BasicTestClass.class);
    }

    @Test
    public void shouldInjectOnIdRef() throws Exception {
        // given
        DeanDefinition integerDeanDefinition = DeanDefinition.defineDean(Integer.class).constructorParams(refByValue(10)).id("integerDean");
        deanContainer.addDeanDefinition(integerDeanDefinition);

        DeanDefinition deanDefinition = DeanDefinition.defineDean(BasicTestClass.class).property("intValue", refById("integerDean"));
        String deanId = deanContainer.addDeanDefinition(deanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(deanId, BasicTestClass.class);

        // then
        assertThat(testObj.getIntValue(), is(10));
    }

    @Test
    public void shouldInjectOnClassRef() throws Exception {
        // given
        DeanDefinition integerDeanDefinition = DeanDefinition.defineDean(Integer.class).constructorParams(refByValue(10));
        deanContainer.addDeanDefinition(integerDeanDefinition);

        DeanDefinition deanDefinition = DeanDefinition.defineDean(BasicTestClass.class).property("intValue", refByClass(Integer.class));
        String deanId = deanContainer.addDeanDefinition(deanDefinition);

        // when
        BasicTestClass testObj = deanContainer.getDeanInstance(deanId, BasicTestClass.class);

        // then
        assertThat(testObj.getIntValue(), is(10));
    }

    @Test
    public void shouldInjectOnParentClassProperty() throws Exception {
        // given
        DeanDefinition integerDeanDefinition = DeanDefinition.defineDean(Integer.class).constructorParams(refByValue(10));
        deanContainer.addDeanDefinition(integerDeanDefinition);

        DeanDefinition deanDefinition = DeanDefinition.defineDean(ChildTestClass.class).property("intValue", refByClass(Integer.class));
        String deanId = deanContainer.addDeanDefinition(deanDefinition);

        // when
        ChildTestClass testObj = deanContainer.getDeanInstance(deanId, ChildTestClass.class);

        // then
        assertThat(testObj.getIntValue(), is(10));
    }

    @Test
    public void shouldInjectFromAssignableDean() throws Exception {
        // given
        DeanDefinition integerDeanDefinition = DeanDefinition.defineDean(Integer.class).constructorParams(refByValue(10));
        deanContainer.addDeanDefinition(integerDeanDefinition);

        DeanDefinition deanDefinition = DeanDefinition.defineDean(ChildTestClass.class).property("numberValue", refByClass(Integer.class));
        String deanId = deanContainer.addDeanDefinition(deanDefinition);

        // when
        ChildTestClass testObj = deanContainer.getDeanInstance(deanId, ChildTestClass.class);

        // then
        assertThat(testObj.getIntValue(), is(0));
        assertThat(testObj.getNumberValue().intValue(), is(10));
    }

    @Test
    public void shouldInjectMultiProperties() throws Exception {
        // given
        DeanDefinition int1DeanDefinition = DeanDefinition.defineDean(Integer.class).constructorParams(refByValue(10)).id("int1");
        deanContainer.addDeanDefinition(int1DeanDefinition);

        DeanDefinition int2DeanDefinition = DeanDefinition.defineDean(Integer.class).constructorParams(refByValue(20)).id("int2");
        deanContainer.addDeanDefinition(int2DeanDefinition);

        DeanDefinition deanDefinition = DeanDefinition.defineDean(BasicTestClass.class).property("numberValue", refById("int1")).property("intValue", refById("int2"));
        String deanId = deanContainer.addDeanDefinition(deanDefinition);

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
