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
        assertTrue(p.isSupported(List.class, new HashSet<String>()));
        
        p = new ListParam(new ArrayList());
        assertFalse(p.isSupported(LinkedList.class, new HashSet<String>()));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testEmptyList() throws Exception
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        List<String> list = new ArrayList<String>();
        ListParam p = new ListParam(list);
        assertEquals("Arrays.asList()", p.toSouceCode(List.class, builder));
        
        Method m = getClass().getMethod("foo1", List.class);
        p = new ListParam(list);
        assertEquals("Arrays.<String>asList()", p.toSouceCode(m.getGenericParameterTypes()[0], builder));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGenericList() throws Exception
    {
        Method m = ListParamTest.class.getMethod("foo2", List.class);
        
        List<List<String>> list = new ArrayList<List<String>>();
        list.add(Collections.<String>emptyList());
        
        ListParam p = new ListParam(list);
        
        foo2(Arrays.<List<String>>asList(new ArrayList<String>()));
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.<List<String>>asList(Arrays.<String>asList())", 
                p.toSouceCode(m.getGenericParameterTypes()[0], builder));
        
        foo2(Arrays.<List<String>>asList(new ArrayList<String>()));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testWildcardList() throws Exception
    {
        Method m = ListParamTest.class.getMethod("foo3", List.class);
        
        List<String> list = new ArrayList<String>();
        
        ListParam p = new ListParam(list);
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.asList()", p.toSouceCode(m.getGenericParameterTypes()[0], builder));
        
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testListOfArrays() throws Exception
    {
        Method m = ListParamTest.class.getMethod("foo4", List.class);
        
        ListParam p = new ListParam(Collections.singletonList(new String[]{"x"}));
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.<String[]>asList(new String[]{\"x\"})", 
                p.toSouceCode(m.getGenericParameterTypes()[0], builder));
    }

 // DONT REMOVE - USED IN TEST
    public void foo1(List<String> list)        { }
    public void foo2(List<List<String>> list)  { }
    public static <T> void foo3(List<T> list)  { }
    public void foo4(List<String[]> list)      { }
}
