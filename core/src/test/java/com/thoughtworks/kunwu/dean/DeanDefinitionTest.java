package com.thoughtworks.kunwu.dean;

import com.thoughtworks.kunwu.container.DeanContainer;
import org.junit.Test;

import static com.thoughtworks.kunwu.dean.DeanReference.refByValue;
import static com.thoughtworks.kunwu.dean.DeanScope.PROTOTYPE;
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
        assertThat(DeanDefinition.defineDirectly(Object.class).getScope(), is(SINGLETON));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionOnIllegalScopeName() throws Exception {
        DeanDefinition.defineDirectly(Object.class).scope("illegal");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfDefineConstructorTwice() throws Exception {
        DeanDefinition deanDefinition = DeanDefinition.defineDirectly(String.class);
        deanDefinition.constructor(refByValue("test"));
        deanDefinition.constructor(refByValue(new char[] {'a', 'b'}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfDefineIdTwice() throws Exception {
        DeanDefinition deanDefinition = DeanDefinition.defineDirectly(String.class);
        deanDefinition.id("test1");
        deanDefinition.id("test2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfDefineScopeTwice() throws Exception {
        DeanDefinition deanDefinition = DeanDefinition.defineDirectly(String.class);
        deanDefinition.scope(SINGLETON);
        deanDefinition.scope(PROTOTYPE);
    }
}
