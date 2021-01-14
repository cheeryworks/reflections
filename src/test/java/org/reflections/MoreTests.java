package org.reflections;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.reflections.scanners.MethodParameterNamesScanner;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.reflections.MoreTestsModel.CyclicAnnotation;
import static org.reflections.MoreTestsModel.Meta;
import static org.reflections.MoreTestsModel.MultiName;
import static org.reflections.MoreTestsModel.Name;
import static org.reflections.MoreTestsModel.Names;
import static org.reflections.MoreTestsModel.ParamNames;
import static org.reflections.MoreTestsModel.SingleName;
import static org.reflections.ReflectionUtilsTest.toStringSorted;
import static org.reflections.ReflectionsTest.are;

public class MoreTests {

    @Test
    public void testCyclicAnnotation() {
        Reflections reflections = new Reflections(MoreTestsModel.class);
        assertThat(reflections.getTypesAnnotatedWith(CyclicAnnotation.class),
                are(CyclicAnnotation.class));
    }

    @Test
    public void noExceptionWhenConfiguredScannerStoreIsEmpty() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage("my.project.prefix"))
                .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
                .filterInputsBy(new FilterBuilder().includePackage("my.project.prefix")));

        reflections.getSubTypesOf(String.class);
    }

    @Test
    public void getAllAnnotatedReturnsMetaAnnotations() {
        Reflections reflections = new Reflections(MoreTestsModel.class);
        for (Class<?> type : reflections.getTypesAnnotatedWith(Meta.class)) {
            Set<Annotation> allAnnotations = ReflectionUtils.getAllAnnotations(type);
            List<? extends Class<? extends Annotation>> collect = allAnnotations.stream().map(Annotation::annotationType).collect(Collectors.toList());
            Assert.assertTrue(collect.contains(Meta.class));
        }

        Meta meta = new Meta() {
            @Override
            public String value() {
                return "a";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Meta.class;
            }
        };
        for (Class<?> type : reflections.getTypesAnnotatedWith(meta)) {
            Set<Annotation> allAnnotations = ReflectionUtils.getAllAnnotations(type);
            List<? extends Class<? extends Annotation>> collect = allAnnotations.stream().map(Annotation::annotationType).collect(Collectors.toList());
            Assert.assertTrue(collect.contains(Meta.class));
        }
    }

    @Test
    @Ignore
    public void testJava9SubtypesOfObject() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClass(Object.class))
                .setScanners(new SubTypesScanner(false)));
        Set<?> components = reflections.getSubTypesOf(Object.class);
        assertFalse(components.isEmpty());
    }

    @Test
    public void testCustomUrlClassLoader() throws MalformedURLException {
        URL externalUrl = new URL("jar:file:" + ReflectionsTest.getUserDir() + "/src/test/resources/another-project.jar!/");
        URLClassLoader externalClassLoader = new URLClassLoader(new URL[]{externalUrl}, Thread.currentThread().getContextClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forClass(TestModel.class))
                .addUrls(externalUrl)
                .addClassLoaders(externalClassLoader));

        assertEquals(toStringSorted(reflections.getSubTypesOf(TestModel.C1.class)),
                "[class another.project.AnotherTestModel$C2, " +
                        "class org.reflections.TestModel$C2, " +
                        "class org.reflections.TestModel$C3, " +
                        "class org.reflections.TestModel$C5]");
    }

    @Test
    public void testReflectionUtilsWithCustomLoader() throws MalformedURLException, ClassNotFoundException {
        URL url = new URL("jar:file:" + ReflectionsTest.getUserDir() + "/src/test/resources/another-project.jar!/");
        final URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());

        Class<?> aClass = Class.forName("another.project.AnotherTestModel$C2", true, classLoader);
        assertEquals(toStringSorted(ReflectionUtils.getAllSuperTypes(aClass)),
                "[class another.project.AnotherTestModel$C2, " +
                        "class org.reflections.TestModel$C1, " +
                        "interface org.reflections.TestModel$I1, " +
                        "interface org.reflections.TestModel$I2]");
    }

    @Test
    public void resourcesScannerFiltersClasses() {
        Reflections reflections = new Reflections(new ResourcesScanner());
        Set<String> keys = reflections.getStore().keys(ResourcesScanner.class.getSimpleName());
        assertTrue(keys.stream().noneMatch(res -> res.endsWith(".class")));
    }

    @Test
    public void testRepeatable() {
        Reflections ref = new Reflections(MoreTestsModel.class);
        Set<Class<?>> clazzes = ref.getTypesAnnotatedWith(Name.class);
        assertTrue(clazzes.contains(SingleName.class));
        assertFalse(clazzes.contains(MultiName.class));

        clazzes = ref.getTypesAnnotatedWith(Names.class);
        assertFalse(clazzes.contains(SingleName.class));
        assertTrue(clazzes.contains(MultiName.class));
    }

    @Test
    public void testMethodParamNamesNotLocalVars() throws NoSuchMethodException {
        Reflections reflections = new Reflections(MoreTestsModel.class, new MethodParameterNamesScanner());

        Class<ParamNames> clazz = ParamNames.class;
        assertEquals(reflections.getConstructorParamNames(clazz.getConstructor(String.class)).toString(),
                "[param1]");
        assertEquals(reflections.getMethodParamNames(clazz.getMethod("test", String.class, String.class)).toString(),
                "[testParam1, testParam2]");
        assertEquals(reflections.getMethodParamNames(clazz.getMethod("test", String.class)).toString(),
                "[testParam]");
        assertEquals(reflections.getMethodParamNames(clazz.getMethod("test2", String.class)).toString(),
                "[testParam]");

    }
}
