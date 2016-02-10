/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;

public class SetParamTest 
{
    @Test
    public void testEquals()
    {
        Set<String> set = Collections.singleton("s1");
        assertEquals(new SetParam(set), new SetParam(set));
        
        assertNotEquals(new SetParam(set), new SetParam(new HashSet<String>()));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testEmptySet() throws Exception
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        SetParam p = new SetParam(new HashSet<String>());
        assertEquals("Collections.emptySet()", p.toSouceCode(Set.class, builder));
        
        Method m = getClass().getMethod("foo", Set.class);
        p = new SetParam(Collections.emptySet());
        assertEquals("Collections.<String>emptySet()",
                p.toSouceCode(m.getGenericParameterTypes()[0], new TestSuiteBuilder()));
    }
    
    @Test
    public void testSupportedType() throws Exception
    {
        Method m = getClass().getMethod("foo", Set.class);
        SetParam p = new SetParam(Collections.emptySet());
        assertTrue(p.isSupported(m.getGenericParameterTypes()[0], new HashSet<String>()));
        
        m = getClass().getMethod("foo3", TreeSet.class);
        p = new SetParam(new TreeSet<String>());
        assertFalse(p.isSupported(m.getGenericParameterTypes()[0], new HashSet<String>()));
    }
    
    @Test
    public void testRawSet() throws Exception
    {
        SetParam p = new SetParam(Collections.singleton("x"));
        testSet(p, Set.class, "asSet(\"x\")", "private static <T> Set<T> asSet(T... elements)  {");
    }
    
    @Test
    public void testGenericSet() throws Exception
    {
        Method m = getClass().getMethod("foo", Set.class);
        SetParam p = new SetParam(Collections.singleton("x"));
        
        testSet(p, m.getGenericParameterTypes()[0],
                "asSet(\"x\")", "private static <T> Set<T> asSet(T... elements)  {");
    }
    
    @Test
    public void testWildcardSet() throws Exception
    {
        Method m = getClass().getMethod("foo1", Set.class);
        SetParam p = new SetParam(Collections.singleton("x"));
        
        testSet(p, m.getGenericParameterTypes()[0],
                "asSet(\"x\")", "private static <T> Set<T> asSet(T... elements)  {");
    }
    
    
    private void testSet(SetParam p, Type targetType, String sourceCode, String helperSignature)
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals(sourceCode, p.toSouceCode(targetType, builder));
        
        List<TestMethod> helperMethods = builder.getHelperMethods();
        assertEquals(1, helperMethods.size());
        assertEquals(helperSignature, getFirstLine(helperMethods.get(0).toSourceCode()));
    }
    
    
    private static String getFirstLine(String text)
    {
        return text.split("\\n")[0];
    }
    
    
 // DONT REMOVE - USED IN TEST
    public void foo(Set<String> set)        { }
    public static <T> void foo1(Set<T> set) { }
    public void foo2(Set<String[]> list)    { }
    public void foo3(TreeSet set)           { }

}
