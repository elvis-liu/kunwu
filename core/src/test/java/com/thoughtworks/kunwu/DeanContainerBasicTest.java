package com.thoughtworks.kunwu;

import org.junit.Before;
import org.junit.Test;

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
        Object obj = deanContainer.deanBuilder(Object.class).create();
        assertThat(obj, notNullValue());
    }

    @Test
    public void shouldBeAbleToGetDeanById() throws Exception {
        deanContainer.addDean("int1", 3);
        assertThat(deanContainer.getDean("int1"), is((Object) 3));
    }

    @Test
    public void shouldAllowDeanWithSameTypeByIds() throws Exception {
        deanContainer.addDean("str1", "test1");
        deanContainer.addDean("str2", "test2");

        assertThat(deanContainer.getDean("str1"), is((Object)"test1"));
        assertThat(deanContainer.getDean("str2"), is((Object)"test2"));
    }

    @Test
    public void shouldAddCreatedObjectAsDean() throws Exception {
        Object createdObj = deanContainer.deanBuilder(Object.class).create();
        assertThat(deanContainer.getDean(Object.class.getSimpleName()), is(createdObj));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowAddDeansWithSameId() throws Exception {
        deanContainer.addDean("str", "test1");
        deanContainer.addDean("str", "test2");
    }
}
