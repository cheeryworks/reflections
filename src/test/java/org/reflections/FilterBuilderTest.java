package org.reflections;

import org.junit.Test;
import org.reflections.util.FilterBuilder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test filtering
 */
public class FilterBuilderTest {

    @Test
    public void testInclude() {
        FilterBuilder filter = new FilterBuilder().include("org\\.reflections.*");
        assertTrue(filter.test("org.reflections.Reflections"));
        assertTrue(filter.test("org.reflections.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
    }

    @Test
    public void testIncludePackage() {
        FilterBuilder filter = new FilterBuilder().includePackage("org.reflections");
        assertTrue(filter.test("org.reflections.Reflections"));
        assertTrue(filter.test("org.reflections.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
    }

    @Test
    public void testIncludePackageMultiple() {
        FilterBuilder filter = new FilterBuilder().includePackage("org.reflections", "org.foo");
        assertTrue(filter.test("org.reflections.Reflections"));
        assertTrue(filter.test("org.reflections.foo.Reflections"));
        assertTrue(filter.test("org.foo.Reflections"));
        assertTrue(filter.test("org.foo.bar.Reflections"));
        assertFalse(filter.test("org.bar.Reflections"));
    }

    @Test
    public void testIncludePackagebyClass() {
        FilterBuilder filter = new FilterBuilder().includePackage(Reflections.class);
        assertTrue(filter.test("org.reflections.Reflections"));
        assertTrue(filter.test("org.reflections.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
    }

    //-----------------------------------------------------------------------
    @Test
    public void testExclude() {
        FilterBuilder filter = new FilterBuilder().exclude("org\\.reflections.*");
        assertFalse(filter.test("org.reflections.Reflections"));
        assertFalse(filter.test("org.reflections.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
    }

    @Test
    public void testExcludePackage() {
        FilterBuilder filter = new FilterBuilder().excludePackage("org.reflections");
        assertFalse(filter.test("org.reflections.Reflections"));
        assertFalse(filter.test("org.reflections.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
    }

    @Test
    public void testExcludePackageMultiple() {
        FilterBuilder filter = new FilterBuilder().excludePackage("org.reflections", "org.foo");
        assertFalse(filter.test("org.reflections.Reflections"));
        assertFalse(filter.test("org.reflections.foo.Reflections"));
        assertFalse(filter.test("org.foo.Reflections"));
        assertFalse(filter.test("org.foo.bar.Reflections"));
        assertTrue(filter.test("org.bar.Reflections"));
    }

    @Test
    public void testExcludePackageByClass() {
        FilterBuilder filter = new FilterBuilder().excludePackage(Reflections.class);
        assertFalse(filter.test("org.reflections.Reflections"));
        assertFalse(filter.test("org.reflections.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
    }

    //-----------------------------------------------------------------------
    @Test
    public void testParseInclude() {
        FilterBuilder filter = FilterBuilder.parse("+org.reflections.*");
        assertTrue(filter.test("org.reflections.Reflections"));
        assertTrue(filter.test("org.reflections.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
        assertTrue(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void testParseIncludeNotRegex() {
        FilterBuilder filter = FilterBuilder.parse("+org.reflections");
        assertFalse(filter.test("org.reflections.Reflections"));
        assertFalse(filter.test("org.reflections.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
        assertFalse(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void testParseExclude() {
        FilterBuilder filter = FilterBuilder.parse("-org.reflections.*");
        assertFalse(filter.test("org.reflections.Reflections"));
        assertFalse(filter.test("org.reflections.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
        assertFalse(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void testParseExcludeNotRegex() {
        FilterBuilder filter = FilterBuilder.parse("-org.reflections");
        assertTrue(filter.test("org.reflections.Reflections"));
        assertTrue(filter.test("org.reflections.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
        assertTrue(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void testParseIncludeExclude() {
        FilterBuilder filter = FilterBuilder.parse("+org.reflections.*, -org.reflections.foo.*");
        assertTrue(filter.test("org.reflections.Reflections"));
        assertFalse(filter.test("org.reflections.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
    }

    //-----------------------------------------------------------------------
    @Test
    public void testParsePackagesInclude() {
        FilterBuilder filter = FilterBuilder.parsePackages("+org.reflections");
        assertTrue(filter.test("org.reflections.Reflections"));
        assertTrue(filter.test("org.reflections.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
        assertFalse(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void testParsePackagesIncludeTrailingDot() {
        FilterBuilder filter = FilterBuilder.parsePackages("+org.reflections.");
        assertTrue(filter.test("org.reflections.Reflections"));
        assertTrue(filter.test("org.reflections.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
        assertFalse(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void testParsePackagesExclude() {
        FilterBuilder filter = FilterBuilder.parsePackages("-org.reflections");
        assertFalse(filter.test("org.reflections.Reflections"));
        assertFalse(filter.test("org.reflections.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
        assertTrue(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void testParsePackagesExcludeTrailingDot() {
        FilterBuilder filter = FilterBuilder.parsePackages("-org.reflections.");
        assertFalse(filter.test("org.reflections.Reflections"));
        assertFalse(filter.test("org.reflections.foo.Reflections"));
        assertTrue(filter.test("org.foobar.Reflections"));
        assertTrue(filter.test("org.reflectionsplus.Reflections"));
    }

    @Test
    public void testParsePackagesIncludeExclude() {
        FilterBuilder filter = FilterBuilder.parsePackages("+org.reflections, -org.reflections.foo");
        assertTrue(filter.test("org.reflections.Reflections"));
        assertFalse(filter.test("org.reflections.foo.Reflections"));
        assertFalse(filter.test("org.foobar.Reflections"));
    }

}
