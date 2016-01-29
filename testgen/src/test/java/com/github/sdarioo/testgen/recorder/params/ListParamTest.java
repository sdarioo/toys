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
    public void testRawList() throws Exception
    {
        List<List<String>> list = new ArrayList<List<String>>();
        
        ListParam p = new ListParam(list);
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.asList()", p.toSouceCode(builder));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGenericList() throws Exception
    {
        Method m = ListParamTest.class.getMethod("foo", List.class);
        assertNotNull(m);
        
        List<List<String>> list = new ArrayList<List<String>>();
        
        ListParam p = new ListParam(list, m.getGenericParameterTypes()[0]);
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.<List<String>>asList()", p.toSouceCode(builder));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testWildcardList() throws Exception
    {
        Method m = ListParamTest.class.getMethod("foo1", List.class);
        assertNotNull(m);
        
        List<String> list = new ArrayList<String>();
        
        ListParam p = new ListParam(list, m.getGenericParameterTypes()[0]);
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.asList()", p.toSouceCode(builder));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testListOfArrays() throws Exception
    {
        Method m = ListParamTest.class.getMethod("foo2", List.class);
        assertNotNull(m);
        
        ListParam p = new ListParam(Collections.emptyList(), m.getGenericParameterTypes()[0]);
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("Arrays.<String[]>asList()", p.toSouceCode(builder));
    }
    
    public void foo(List<List<String>> list)
    {
    }
    public static <T> void foo1(List<T> list)
    {
    }
    public void foo2(List<String[]> list)
    {
    }
}
