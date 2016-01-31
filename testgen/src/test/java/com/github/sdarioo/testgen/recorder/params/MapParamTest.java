package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class MapParamTest
{
    @Test
    public void testIsMapSupported() throws Exception
    {
        Method m = MapParamTest.class.getMethod("foo", Map.class);
        MapParam p = new MapParam(new HashMap(), m.getGenericParameterTypes()[0]);
        assertTrue(p.isSupported(new HashSet<String>()));
        
        m = MapParamTest.class.getMethod("foo1", TreeMap.class);
        p = new MapParam(new HashMap(), m.getGenericParameterTypes()[0]);
        assertFalse(p.isSupported(new HashSet<String>()));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testEquals()
    {
        Map<Integer, String> map = new HashMap<Integer, String>();
        assertEquals(new MapParam(map), new MapParam(map));
        
        map.put(1, "value");
        assertEquals(new MapParam(map), new MapParam(map));
        
        assertNotEquals(new MapParam(Collections.emptyMap()), new MapParam(map));
    }
    
    @Test
    public void testRawMap() throws Exception
    {
        
    }
    
    @Test
    public void testGenericMap() throws Exception
    {
        Method m = MapParamTest.class.getMethod("foo", Map.class);
        MapParam p = new MapParam(Collections.singletonMap(1, "1"), m.getGenericParameterTypes()[0]);
        assertEquals("asMap(asPair(1, \"1\"))", p.toSouceCode(new TestSuiteBuilder()));
        
        fail("fixme");
        //foo(asMap(asPair(1, "1")));
    }
    

    
}
