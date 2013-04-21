package com.thoughtworks.kunwu.context;

import com.thoughtworks.kunwu.container.CoreDeanContainer;
import com.thoughtworks.kunwu.container.DeanContainer;
import com.thoughtworks.kunwu.context.config_inject.TestConfigWithConstructorInject;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Sets.newHashSet;
import static com.thoughtworks.kunwu.dean.DeanDefinition.getDeanDefaultName;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class PackageBasedDeanContextTest {

    private DeanContainer deanContainer;

    @Before
    public void setUp() throws Exception {
        deanContainer = new CoreDeanContainer();
    }

    @Test
    public void shouldFindAllDeanConfigurationsUnderGivenPackageRecursively() throws Exception {
        // given
        PackageBasedDeanContext deanContext = new PackageBasedDeanContext(
                newHashSet("com.thoughtworks.kunwu.context.simple_configs"));

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

    @Test
    public void shouldNotAddConfigClassAsDean() throws Exception {
        // given
        deanContainer.addDeanInstance("stringDean", "testString");
        deanContainer.addDeanInstance("integerDean", 13);
        PackageBasedDeanContext deanContext = new PackageBasedDeanContext(
                newHashSet("com.thoughtworks.kunwu.context.config_inject"), deanContainer);

        // when
        deanContext.scanAll();

        // then
        assertThat(deanContext.getDeanDefinition(getDeanDefaultName(TestConfigWithConstructorInject.class)), nullValue());
    }

    @Test
    public void shouldAllowConstructorInjectIntoConfigClass() throws Exception {
        // given
        deanContainer.addDeanInstance("stringDean", "testString");
        deanContainer.addDeanInstance("integerDean", 13);
        PackageBasedDeanContext deanContext = new PackageBasedDeanContext(
                newHashSet("com.thoughtworks.kunwu.context.config_inject"), deanContainer);

        // when
        deanContext.scanAll();

        // then
        assertThat(deanContext.getDeanInstance("testDeanA", String.class), is("testString_testA"));
    }

    @Test
    public void shouldAllowPropertyInjectIntoConfigClass() throws Exception {
        // given
        deanContainer.addDeanInstance("stringDean", "testString");
        deanContainer.addDeanInstance("integerDean", 13);
        PackageBasedDeanContext deanContext = new PackageBasedDeanContext(
                newHashSet("com.thoughtworks.kunwu.context.config_inject"), deanContainer);

        // when
        deanContext.scanAll();

        // then
        assertThat(deanContext.getDeanInstance("testDeanB", Integer.class), is(14));
    }
}
