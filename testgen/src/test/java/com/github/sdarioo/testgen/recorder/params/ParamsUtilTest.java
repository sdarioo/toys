/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ParamsUtilTest 
{
    public List rawList;
    public List<List<Map<String, List<String>>>> map;
    
    
    
    @SuppressWarnings("nls")
    @Test
    public void testGetGenericTypeName() throws Exception
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        assertEquals(null, builder.getGenericTypeName(null));
        
        
        Field field = getClass().getField("rawList");
        Type type = field.getGenericType();
        assertEquals("List", builder.getGenericTypeName(type));
        
        field = getClass().getField("map");
        type = field.getGenericType();
        assertEquals("List<List<Map<String, List<String>>>>", builder.getGenericTypeName(type));
    }
}
