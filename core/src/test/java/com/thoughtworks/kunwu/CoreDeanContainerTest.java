package com.thoughtworks.kunwu;

import org.junit.Before;
import org.junit.Test;

import static com.thoughtworks.kunwu.DeanScope.PROTOTYPE;
import static com.thoughtworks.kunwu.reference.DeanReference.refByValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class CoreDeanContainerTest {

    private DeanContainer deanContainer;

    @Before
    public void setUp() throws Exception {
        deanContainer = new CoreDeanContainer();
    }

    @Test
    public void shouldCreatePOJOWithOnlyDefaultConstructor() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(Object.class);
        String id = deanContainer.addDeanDefinition(deanDefinition);
        assertThat(deanContainer.getDeanInstance(id), notNullValue());
    }

    @Test
    public void shouldBeAbleToGetDeanById() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(Integer.class).id("int1").constructBy(refByValue(3));
        deanContainer.addDeanDefinition(deanDefinition);
        assertThat(deanContainer.getDeanInstance("int1"), is((Object) 3));
    }

    @Test
    public void shouldGetDeanInstanceWithType() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(Integer.class)
                .id("int1").constructBy(refByValue(3));
        deanContainer.addDeanDefinition(deanDefinition);

        assertThat(deanContainer.getDeanInstance("int1", Integer.class), is(3));
    }

    @Test(expected = ClassCastException.class)
    public void shouldThrowExceptionWhenGetDeanInstanceWithWrongType() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(Integer.class)
                .id("int1").constructBy(refByValue(3));
        deanContainer.addDeanDefinition(deanDefinition);

        deanContainer.getDeanInstance("int1", String.class);
    }

    @Test
    public void shouldGetDeanInstanceWithAssignableType() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(Integer.class)
                .id("int1").constructBy(refByValue(3));
        deanContainer.addDeanDefinition(deanDefinition);

        assertThat(deanContainer.getDeanInstance("int1", Number.class).intValue(), is(3));
    }

    @Test
    public void shouldAllowDeanWithSameTypeByIds() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(String.class)
                .id("str1").constructBy(refByValue("test1"));
        deanContainer.addDeanDefinition(deanDefinition);
        deanDefinition = new DeanDefinition(String.class)
                .id("str2").constructBy(refByValue("test2"));
        deanContainer.addDeanDefinition(deanDefinition);

        assertThat(deanContainer.getDeanInstance("str1", String.class), is("test1"));
        assertThat(deanContainer.getDeanInstance("str2", String.class), is("test2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowAddDeansWithSameId() throws Exception {
        deanContainer.addDeanDefinition(new DeanDefinition(Object.class).id("dean"));
        deanContainer.addDeanDefinition(new DeanDefinition(Object.class).id("dean"));
    }

    @Test
    public void addedDeanDefinitionCannotBeAffected() throws Exception {
        DeanDefinition deanDefinition = new DeanDefinition(String.class)
                .id("str1").constructBy(refByValue("original"));
        deanContainer.addDeanDefinition(deanDefinition);

        deanDefinition.constructBy(refByValue("modified"));

        assertThat(deanContainer.getDeanInstance("str1", String.class), is("original"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGetDeanDefinitionWithNotExistedId() throws Exception {
        deanContainer.getDeanDefinition("notExisted");
    }

    @Test
    public void shouldDefaultToSingletonScope() throws Exception {
        // given
        DeanDefinition deanDefinition = new DeanDefinition(Object.class).id("dean");
        deanContainer.addDeanDefinition(deanDefinition);

        // when
        Object obj1 = deanContainer.getDeanInstance("dean");
        Object obj2 = deanContainer.getDeanInstance("dean");

        // then
        assertThat(obj1, notNullValue());
        assertThat(obj1 == obj2, is(true));
    }

    @Test
    public void shouldSupportPrototypeScope() throws Exception {
        // given
        DeanDefinition deanDefinition = new DeanDefinition(Object.class).id("dean").scope(PROTOTYPE);
        deanContainer.addDeanDefinition(deanDefinition);

        // when
        Object obj1 = deanContainer.getDeanInstance("dean");
        Object obj2 = deanContainer.getDeanInstance("dean");

        // then
        assertThat(obj1, notNullValue());
        assertThat(obj2, notNullValue());
        assertThat(obj1 == obj2, is(false));
    }
}
