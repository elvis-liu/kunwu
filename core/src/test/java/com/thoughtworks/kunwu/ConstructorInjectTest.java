package com.thoughtworks.kunwu;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ConstructorInjectTest {
    @Test
    public void shouldInjectPOJOWithSingleConstructorSingleParam() throws Exception {
        // given
        DeanContainer deanContainer = new DeanContainer();
        TestDeanA dean = new TestDeanA();
        dean.setValue(100);
        deanContainer.addDean(dean);

        // when
        TestClassA testPOJO = deanContainer.create(TestClassA.class);

        // then
        assertThat(testPOJO.getDean().getValue(), is(100));
    }

    private static class TestClassA {
        private TestDeanA dean;

        public TestClassA(TestDeanA dean) {
            this.dean = dean;
        }

        private TestDeanA getDean() {
            return dean;
        }
    }

    private static class TestDeanA {
        private int value;

        private int getValue() {
            return value;
        }

        private void setValue(int value) {
            this.value = value;
        }
    }
}
