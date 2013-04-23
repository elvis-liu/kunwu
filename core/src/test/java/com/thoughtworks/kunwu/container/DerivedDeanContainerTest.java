package com.thoughtworks.kunwu.container;

import com.thoughtworks.kunwu.dean.DeanDefinition;
import com.thoughtworks.kunwu.exception.NoSuchDeanException;
import org.junit.Before;
import org.junit.Test;

import static com.thoughtworks.kunwu.dean.DeanReference.refByValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DerivedDeanContainerTest {

    private DeanContainer parentContainer;
    private DeanContainer childContainer;

    @Before
    public void setUp() throws Exception {
        parentContainer = new CoreDeanContainer();
        childContainer = new DerivedDeanContainer(parentContainer);
    }

    @Test
    public void shouldChildContainerSeeDeansWithinParent() throws Exception {
        // given
        DeanDefinition deanA = DeanDefinition.defineDean(String.class).constructorParams(refByValue("inParent")).id("deanA");
        parentContainer.addDeanDefinition(deanA);

        DeanDefinition deanB = DeanDefinition.defineDean(String.class).constructorParams(refByValue("inChild")).id("deanB");
        childContainer.addDeanDefinition(deanB);

        // then
        assertThat(parentContainer.getDeanInstance("deanA", String.class), is("inParent"));
        assertThat(childContainer.getDeanInstance("deanA", String.class), is("inParent"));
        assertThat(childContainer.getDeanInstance("deanB", String.class), is("inChild"));
    }

    @Test(expected = NoSuchDeanException.class)
    public void shouldNotSeeDeansWithinChildContainerFromParent() throws Exception {
        // given
        DeanDefinition deanA = DeanDefinition.defineDean(String.class).constructorParams(refByValue("inParent")).id("deanA");
        parentContainer.addDeanDefinition(deanA);

        DeanDefinition deanB = DeanDefinition.defineDean(String.class).constructorParams(refByValue("inChild")).id("deanB");
        childContainer.addDeanDefinition(deanB);

        // then
        parentContainer.getDeanInstance("deanB");
    }

    @Test
    public void shouldOverrideDeanWithSameIdInParent() throws Exception {
        // given
        DeanDefinition deanA = DeanDefinition.defineDean(String.class).constructorParams(refByValue("inParent")).id("dean");
        parentContainer.addDeanDefinition(deanA);

        DeanDefinition deanB = DeanDefinition.defineDean(String.class).constructorParams(refByValue("inChild")).id("dean");
        childContainer.addDeanDefinition(deanB);

        // then
        assertThat(parentContainer.getDeanInstance("dean", String.class), is("inParent"));
        assertThat(childContainer.getDeanInstance("dean", String.class), is("inChild"));
    }
}
