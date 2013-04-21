package com.thoughtworks.kunwu.context;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class PackageBasedDeanContextTest {

    private PackageBasedDeanContext deanContext;

    @Before
    public void setUp() throws Exception {
        deanContext = new PackageBasedDeanContext(Sets.newHashSet("com.thoughtworks.kunwu.context.simple_configs"));
    }

    @Test
    public void shouldFindAllDeanConfigurationsUnderGivenPackageRecursively() throws Exception {
        // when
        deanContext.scanAll();

        // then
        assertThat(deanContext.getDeanDefinition("testDeanA"), notNullValue());
        assertThat(deanContext.getDeanInstance("testDeanA", String.class), is("testA"));
        assertThat(deanContext.getDeanDefinition("testDeanB"), notNullValue());
        assertThat(deanContext.getDeanInstance("testDeanB", Integer.class), is(13));
        assertThat(deanContext.getDeanDefinition("testDeanC"), notNullValue());
        assertThat(deanContext.getDeanInstance("testDeanC", String.class), is("testC"));
        assertThat(deanContext.getDeanDefinition("testDeanD"), notNullValue());
        assertThat(deanContext.getDeanInstance("testDeanD", String.class), is("testD"));
    }
}
