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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

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
    public void testEmptySet()
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        SetParam p = new SetParam(new HashSet<String>());
        assertEquals("new HashSet()", p.toSouceCode(builder));
        
        p = new SetParam(Collections.singleton("s1"));
        assertEquals("asSet(\"s1\")", p.toSouceCode(builder));
    }
    
    @Test
    public void testSupportedType() throws Exception
    {
        Method m = getClass().getMethod("foo", Set.class);
        SetParam p = new SetParam(Collections.emptySet(), m.getGenericParameterTypes()[0]);
        assertTrue(p.isSupported(new HashSet<String>()));
        
        m = getClass().getMethod("foo3", TreeSet.class);
        p = new SetParam(new TreeSet<String>(), m.getGenericParameterTypes()[0]);
        assertFalse(p.isSupported(new HashSet<String>()));
    }
    
    @Test
    public void testGenericSet() throws Exception
    {
        Method m = getClass().getMethod("foo", Set.class);
        SetParam p = new SetParam(Collections.emptySet(), m.getGenericParameterTypes()[0]);
        assertEquals("new HashSet<String>()", p.toSouceCode(new TestSuiteBuilder()));
        
        p = new SetParam(Collections.singleton("x"), m.getGenericParameterTypes()[0]);
        assertEquals("asSet(\"x\")", p.toSouceCode(new TestSuiteBuilder()));
    }
    
    public void foo(Set<String> set)
    {
    }
    public static <T> void foo1(Set<T> set)
    {
    }
    public void foo2(Set<String[]> list)
    {
    }
    public void foo3(TreeSet set)
    {
    }

}
