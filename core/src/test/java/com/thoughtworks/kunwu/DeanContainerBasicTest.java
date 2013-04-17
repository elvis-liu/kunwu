package com.thoughtworks.kunwu;

import org.junit.Before;
import org.junit.Test;

import static com.thoughtworks.kunwu.reference.DeanReference.refByValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DeanContainerBasicTest {

    private DeanContainer deanContainer;

    @Before
    public void setUp() throws Exception {
        deanContainer = new DeanContainer();
    }

    @Test
    public void shouldCreatePOJOWithOnlyDefaultConstructor() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(Object.class);
        String id = deanContainer.addDean(deanDefinition);
        assertThat(deanContainer.getDeanInstance(id), notNullValue());
    }

    @Test
    public void shouldBeAbleToGetDeanById() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(Integer.class).id("int1").constructBy(refByValue(3));
        deanContainer.addDean(deanDefinition);
        assertThat(deanContainer.getDeanInstance("int1"), is((Object) 3));
    }

    @Test
    public void shouldGetDeanInstanceWithType() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(Integer.class)
                .id("int1").constructBy(refByValue(3));
        deanContainer.addDean(deanDefinition);

        assertThat(deanContainer.getDeanInstance("int1", Integer.class), is(3));
    }

    @Test(expected = ClassCastException.class)
    public void shouldThrowExceptionWhenGetDeanInstanceWithWrongType() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(Integer.class)
                .id("int1").constructBy(refByValue(3));
        deanContainer.addDean(deanDefinition);

        deanContainer.getDeanInstance("int1", String.class);
    }

    @Test
    public void shouldGetDeanInstanceWithAssignableType() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(Integer.class)
                .id("int1").constructBy(refByValue(3));
        deanContainer.addDean(deanDefinition);

        assertThat(deanContainer.getDeanInstance("int1", Number.class).intValue(), is(3));
    }

    @Test
    public void shouldAllowDeanWithSameTypeByIds() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(String.class)
                .id("str1").constructBy(refByValue("test1"));
        deanContainer.addDean(deanDefinition);
        deanDefinition = new DeanDefinition(String.class)
                .id("str2").constructBy(refByValue("test2"));
        deanContainer.addDean(deanDefinition);

        assertThat(deanContainer.getDeanInstance("str1", String.class), is("test1"));
        assertThat(deanContainer.getDeanInstance("str2", String.class), is("test2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowAddDeansWithSameId() throws Exception {
        deanContainer.addDean(new DeanDefinition(Object.class).id("dean"));
        deanContainer.addDean(new DeanDefinition(Object.class).id("dean"));
    }

    @Test
    public void addedDeanDefinitionCannotBeAffected() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(String.class)
                .id("str1").constructBy(refByValue("original"));
        deanContainer.addDean(deanDefinition);

        deanDefinition.constructBy(refByValue("modified"));

        assertThat(deanContainer.getDeanInstance("str1", String.class), is("original"));
    }
}
