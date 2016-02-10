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

public class MapParamTest
{
    @Test
    public void testIsMapSupported() throws Exception
    {
        Method m = MapParamTest.class.getMethod("foo1", Map.class);
        MapParam p = new MapParam(new HashMap());
        assertTrue(p.isSupported(m.getGenericParameterTypes()[0], new HashSet<String>()));
        
        m = MapParamTest.class.getMethod("foo2", TreeMap.class);
        p = new MapParam(new HashMap());
        assertFalse(p.isSupported(m.getGenericParameterTypes()[0], new HashSet<String>()));
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
        MapParam p = new MapParam(Collections.emptyMap());
        testMap(p, Map.class, "asMap()", "private static Map asMap(Object[]... pairs) {");
    }
    
    @Test
    public void testGenericMap() throws Exception
    {
        Method m = MapParamTest.class.getMethod("foo1", Map.class);
        MapParam p = new MapParam(Collections.emptyMap());
        
        testMap(p, m.getGenericParameterTypes()[0], 
                "asMap()", "private static Map<Integer, String> asMap(Object[]... pairs) {");
    }
    
    @Test
    public void testWildcardMap() throws Exception
    {
        Method m = MapParamTest.class.getMethod("foo3", Map.class);
        MapParam p = new MapParam(Collections.<Integer, String>emptyMap());
        
        testMap(p, m.getGenericParameterTypes()[0], 
                "asMap()", "private static Map asMap(Object[]... pairs) {");
    }
    
    @Test
    public void testMapOfLists() throws Exception
    {
        Method m = MapParamTest.class.getMethod("foo4", Map.class);
        MapParam p = new MapParam(Collections.<Integer, String>emptyMap());
        
        testMap(p, m.getGenericParameterTypes()[0],
                "asMap()", "private static Map<Integer, List<String>> asMap(Object[]... pairs) {");
    }
    
    private void testMap(MapParam p, Type targetType, String sourceCode, String expectedSignature)
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals(sourceCode, p.toSouceCode(targetType, builder));
        
        List<TestMethod> helperMethods = builder.getHelperMethods();
        assertEquals(2, helperMethods.size());
        
        boolean found = false;
        for (TestMethod helperMethod : helperMethods) {
            if (helperMethod.getName().equals("asMap")) {
                found = true;
                String signature = getFirstLine(helperMethod.toSourceCode());
                assertEquals(expectedSignature, signature);
            }
        }
        assertTrue("asMap not found", found);
    }

    private static String getFirstLine(String text)
    {
        return text.split("\\n")[0];
    }

    // DONT REMOVE - USED IN TEST 
    public void foo1(Map<Integer, String> map)   {}
    public void foo2(TreeMap map)                {}
    public static <K,V> void foo3(Map<K,V> map)  {}
    public void foo4(Map<Integer, List<String>> map)   {}
    // DONT REMOVE - USED IN TEST
    
    

}
