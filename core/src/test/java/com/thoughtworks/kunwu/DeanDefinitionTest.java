package com.thoughtworks.kunwu;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DeanDefinitionTest {
    @Test
    public void testDefaultDeanId() throws Exception {
        assertThat(DeanDefinition.getDeanDefaultName(Object.class), is("object"));
        assertThat(DeanDefinition.getDeanDefaultName(Integer.class), is("integer"));
        assertThat(DeanDefinition.getDeanDefaultName(DeanInstanceBuilder.class), is("deanBuilder"));
        assertThat(DeanDefinition.getDeanDefaultName((new int[0]).getClass()), is("int[]"));
        assertThat(DeanDefinition.getDeanDefaultName((new Object() {}).getClass()), is(""));
    }
}
