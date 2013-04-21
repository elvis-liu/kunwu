package com.thoughtworks.kunwu.dean;

import com.thoughtworks.kunwu.annotation.DeanIdRef;
import com.thoughtworks.kunwu.annotation.DeanInject;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.Map;

import static com.thoughtworks.kunwu.dean.DeanReference.refByClass;
import static com.thoughtworks.kunwu.dean.DeanReference.refById;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class AnnotationBasedDeanDefinitionTest {
    @Test
    public void shouldParseAnnotatedConstructorsInject() throws Exception {
        // given
        DeanDefinition deanDefinition = DeanDefinition.defineDeanByAnnotation(SimpleConstructorInject.class);
        Constructor<?> targetConstructor = SimpleConstructorInject.class.getConstructor(int.class);

        // then
        assertEquals(targetConstructor, deanDefinition.getConstructor());
        assertThat(deanDefinition.getConstructorParamRefs().length, is(1));
        assertThat(deanDefinition.getConstructorParamRefs()[0], is(refByClass(int.class)));
    }

    @Test
    public void shouldParseAnnotatedPropertiesInject() throws Exception {
        // given
        DeanDefinition deanDefinition = DeanDefinition.defineDeanByAnnotation(SimplePropertyInject.class);

        // when
        Map<String, DeanReference> propertyRefs = deanDefinition.getPropertyRefMap();

        // then
        assertThat(propertyRefs.size(), is(2));
        assertThat(propertyRefs.get("intValue"), is(refByClass(int.class)));
        assertThat(propertyRefs.get("stringValue"), is(refByClass(String.class)));
    }

    @Test
    public void shouldParseAnnotatedConstructorInjectWithIdRef() throws Exception {
        // given
        DeanDefinition deanDefinition = DeanDefinition.defineDeanByAnnotation(IdRefConstructorInject.class);
        Constructor<?> targetConstructor = IdRefConstructorInject.class.getConstructor(String.class);

        // then
        assertEquals(targetConstructor, deanDefinition.getConstructor());
        assertThat(deanDefinition.getConstructorParamRefs().length, is(1));
        assertThat(deanDefinition.getConstructorParamRefs()[0], is(refById("stringDean")));
    }

    @Test
    public void shouldParseAnnotatedPropertyInjectWithIdRef() throws Exception {
        // given
        DeanDefinition deanDefinition = DeanDefinition.defineDeanByAnnotation(IdRefPropertyInject.class);

        // when
        Map<String, DeanReference> propertyRefs = deanDefinition.getPropertyRefMap();

        // then
        assertThat(propertyRefs.size(), is(1));
        assertThat(propertyRefs.get("intValue"), is(refById("intDean")));
    }

    public static class SimpleConstructorInject {
        @SuppressWarnings("unused")
        @DeanInject
        public SimpleConstructorInject(int intValue) {
        }

        @SuppressWarnings("unused")
        public SimpleConstructorInject(long longValue) {
        }
    }

    public static class SimplePropertyInject {
        @SuppressWarnings("unused")
        @DeanInject
        private int intValue;

        @SuppressWarnings("unused")
        @DeanInject
        private String stringValue;

        @SuppressWarnings("unused")
        private long longValue;
    }

    public static class IdRefConstructorInject {
        @SuppressWarnings("unused")
        @DeanInject()
        public IdRefConstructorInject(@DeanIdRef("stringDean") String stringValue) {
        }
    }

    public static class IdRefPropertyInject {
        @SuppressWarnings("unused")
        @DeanInject
        @DeanIdRef("intDean")
        private int intValue;
    }
}
