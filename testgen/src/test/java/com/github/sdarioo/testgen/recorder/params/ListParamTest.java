/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ListParamTest 
{
    
    @Test
    public void testSupportedList()
    {
        ListParam p = new ListParam(new ArrayList());
        assertTrue(p.isSupported(new HashSet<String>()));
        
        p = new ListParam(new ArrayList(), LinkedList.class);
        assertFalse(p.isSupported(new HashSet<String>()));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testEmptyList() throws Exception
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        List<String> list = new ArrayList<String>();
        ListParam p = new ListParam(list);
        assertEquals("new ArrayList()", p.toSouceCode(builder));
        
        Method m = getClass().getMethod("foo1", List.class);
        p = new ListParam(list, m.getGenericParameterTypes()[0]);
        assertEquals("new ArrayList<String>()", p.toSouceCode(builder));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGenericList() throws Exception
    {
        Method m = ListParamTest.class.getMethod("foo2", List.class);
        assertNotNull(m);
        
        List<List<String>> list = new ArrayList<List<String>>();
        list.add(Collections.<String>emptyList());
        
        ListParam p = new ListParam(list, m.getGenericParameterTypes()[0]);
        
        foo2(Arrays.<List<String>>asList(new ArrayList<String>()));
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.<List<String>>asList(new ArrayList<String>())", p.toSouceCode(builder));
        
        foo2(Arrays.<List<String>>asList(new ArrayList<String>()));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testWildcardList() throws Exception
    {
        Method m = ListParamTest.class.getMethod("foo3", List.class);
        assertNotNull(m);
        
        List<String> list = new ArrayList<String>();
        
        ListParam p = new ListParam(list, m.getGenericParameterTypes()[0]);
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new ArrayList()", p.toSouceCode(builder));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testListOfArrays() throws Exception
    {
        Method m = ListParamTest.class.getMethod("foo4", List.class);
        assertNotNull(m);
        
        ListParam p = new ListParam(Collections.singletonList(new String[]{"x"}), m.getGenericParameterTypes()[0]);
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.<String[]>asList(new String[]{\"x\"})", p.toSouceCode(builder));
        
        foo4(Arrays.<String[]>asList(new String[]{"x"}));
    }
    

    
    public void foo1(List<String> list)
    {
    }
    public void foo2(List<List<String>> list)
    {
    }
    public static <T> void foo3(List<T> list)
    {
    }
    public void foo4(List<String[]> list)
    {
    }
}
