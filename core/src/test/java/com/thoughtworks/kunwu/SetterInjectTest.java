package com.thoughtworks.kunwu;

import org.junit.Before;
import org.junit.Test;

import static com.thoughtworks.kunwu.reference.DeanReference.refByClass;
import static com.thoughtworks.kunwu.reference.DeanReference.refById;
import static com.thoughtworks.kunwu.reference.DeanReference.refByValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SetterInjectTest {

    private DeanContainer deanContainer;

    @Before
    public void setUp() throws Exception {
        deanContainer = new DeanContainer();
    }

    @Test
    public void shouldInjectOnValueRef() throws Exception {
        // when
        BasicTestClass testObj = deanContainer.deanBuilder(BasicTestClass.class).property("intValue", refByValue(3)).create();

        // then
        assertThat(testObj.getIntValue(), is(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotInjectOnIllegalProperty() throws Exception {
        deanContainer.deanBuilder(BasicTestClass.class).property("notExisted", refByValue(3)).create();
    }

    @Test
    public void shouldInjectOnIdRef() throws Exception {
        // given
        deanContainer.deanBuilder(Integer.class).constructBy(refByValue(10)).id("integerDean").create();

        // when
        BasicTestClass testObj = deanContainer.deanBuilder(BasicTestClass.class).property("intValue", refById("integerDean")).create();

        // then
        assertThat(testObj.getIntValue(), is(10));
    }

    @Test
    public void shouldInjectOnClassRef() throws Exception {
        // given
        deanContainer.deanBuilder(Integer.class).constructBy(refByValue(10)).create();

        // when
        BasicTestClass testObj = deanContainer.deanBuilder(BasicTestClass.class).property("intValue", refByClass(Integer.class)).create();

        // then
        assertThat(testObj.getIntValue(), is(10));
    }

    @Test
    public void shouldInjectOnParentClassProperty() throws Exception {
        // given
        deanContainer.deanBuilder(Integer.class).constructBy(refByValue(10)).create();

        // when
        ChildTestClass testObj = deanContainer.deanBuilder(ChildTestClass.class).property("intValue", refByClass(Integer.class)).create();

        // then
        assertThat(testObj.getIntValue(), is(10));
    }

    @Test
    public void shouldInjectFromAssignableDean() throws Exception {
        // given
        deanContainer.deanBuilder(Integer.class).constructBy(refByValue(10)).id("integerDean").create();

        // when
        BasicTestClass testObj = deanContainer.deanBuilder(BasicTestClass.class).property("numberValue", refById("integerDean")).create();

        // then
        assertThat(testObj.getIntValue(), is(0));
        assertThat(testObj.getNumberValue().intValue(), is(10));
    }

    @Test
    public void shouldInjectMultiProperties() throws Exception {
        // given
        deanContainer.deanBuilder(Integer.class).constructBy(refByValue(10)).id("int1").create();
        deanContainer.deanBuilder(Integer.class).constructBy(refByValue(20)).id("int2").create();

        // when
        BasicTestClass testObj = deanContainer.deanBuilder(BasicTestClass.class)
                .property("intValue", refById("int1"))
                .property("numberValue", refById("int2"))
                .create();

        // then
        assertThat(testObj.getIntValue(), is(10));
        assertThat(testObj.getNumberValue().intValue(), is(20));
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
