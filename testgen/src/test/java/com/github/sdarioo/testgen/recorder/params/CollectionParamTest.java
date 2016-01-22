/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class CollectionParamTest
{
    @SuppressWarnings("nls")
    @Test
    public void testListParam()
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        List<String> list = new ArrayList<String>();
        ListParam p = new ListParam(list);
        assertEquals("Arrays.asList()", p.toSouceCode(builder));
        
        list.add("s1");
        p = new ListParam(list);
        assertEquals("Arrays.asList(\"s1\")", p.toSouceCode(builder));
        
        assertEquals(new ListParam(list), new ListParam(list));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testSetParam()
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        Set<String> set = new HashSet<String>();
        SetParam p = new SetParam(set);
        assertEquals("new HashSet(Arrays.asList())", p.toSouceCode(builder));
        
        set.add("s1");
        p = new SetParam(set);
        assertEquals("new HashSet(Arrays.asList(\"s1\"))", p.toSouceCode(builder));
        
        assertEquals(new SetParam(set), new SetParam(set));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testMapParam()
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        Map<Integer, String> map = new HashMap<Integer, String>();
        MapParam p = new MapParam(map);
        assertEquals("asMap()", p.toSouceCode(builder));
        
        map.put(1, "value");
        p = new MapParam(map);
        assertEquals("asMap(asPair(1, \"value\"))", p.toSouceCode(builder));
        
        assertEquals(new MapParam(map), new MapParam(map));
        
    }
}
