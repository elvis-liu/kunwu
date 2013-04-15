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
        Object obj = deanContainer.deanBuilder(Object.class).constructBy().create();
        assertThat(obj, notNullValue());
    }

    @Test
    public void shouldCreateExistedDeanObjectWhenClassMatches() throws Exception {
        Integer intDean = new Integer(3);
        deanContainer.addDean(intDean);
        assertThat(deanContainer.deanBuilder(Integer.class).constructBy().create(), is(intDean));
    }

    @Test
    public void shouldAddCreatedObjectAsDean() throws Exception {
        Object createdObj = deanContainer.deanBuilder(Object.class).constructBy().create();
        assertThat(deanContainer.getDean(Object.class), is(createdObj));
    }
}
