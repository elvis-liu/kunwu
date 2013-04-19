package com.thoughtworks.kunwu.dean;

import com.thoughtworks.kunwu.container.DeanContainer;
import org.junit.Test;

import static com.thoughtworks.kunwu.dean.DeanScope.SINGLETON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DeanDefinitionTest {
    @Test
    public void testDefaultDeanId() throws Exception {
        assertThat(DeanDefinition.getDeanDefaultName(Object.class), is("object"));
        assertThat(DeanDefinition.getDeanDefaultName(Integer.class), is("integer"));
        assertThat(DeanDefinition.getDeanDefaultName(DeanContainer.class), is("deanContainer"));
        assertThat(DeanDefinition.getDeanDefaultName((new int[0]).getClass()), is("int[]"));
        assertThat(DeanDefinition.getDeanDefaultName((new Object() {}).getClass()), is(""));
    }

    @Test
    public void testDefaultScope() throws Exception {
        assertThat(new DeanDefinition(Object.class).getScope(), is(SINGLETON));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionOnIllegalScopeName() throws Exception {
        new DeanDefinition(Object.class).scope("illegal");
    }
}
