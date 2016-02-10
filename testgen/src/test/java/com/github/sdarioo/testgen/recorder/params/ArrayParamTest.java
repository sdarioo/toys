package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ArrayParamTest 
{
    @Test
    public void testEmptyArray()
    {
        ArrayParam p = new ArrayParam(new Properties[0]);
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new Properties[]{}", p.toSouceCode(Properties[].class, builder));
    }
    
    @Test
    public void testIntArray()
    {
        ArrayParam p = new ArrayParam(new int[] {1, 2, 3});
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new int[]{1, 2, 3}", p.toSouceCode(int[].class, builder));
    }
    
    @Test
    public void testStringArray()
    {
        ArrayParam p = new ArrayParam(new String[] {"ala", "ma", "kota"});
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new String[]{\"ala\", \"ma\", \"kota\"}", p.toSouceCode(String[].class, builder));
    }
    
    @Test
    public void testTwoDimArray()
    {
        ArrayParam p = new ArrayParam(new String[][] { new String[]{"ala"}, new String[]{"ma"}});
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new String[][]{new String[]{\"ala\"}, new String[]{\"ma\"}}", 
                p.toSouceCode(String[][].class, builder));
    }
    
    @Test
    public void testGenericArray() throws Exception
    {
        Method m = ArrayParamTest.class.getMethod("foo", List[].class);
        assertNotNull(m);
        
        ArrayParam p = new ArrayParam(new ArrayList[0]);
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new List[]{}", p.toSouceCode(m.getGenericParameterTypes()[0], builder));
    }
    
    @Test
    public void testTypeVarArray() throws Exception
    {
        System.err.println(String[].class.getName());
        
        Method m = ArrayParamTest.class.getMethod("foo", Object[].class);
        assertNotNull(m);
        
        ArrayParam p = new ArrayParam(new String[] {"goo"});
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new String[]{\"goo\"}", p.toSouceCode(m.getGenericParameterTypes()[0], builder));
    }
    
    public static <T> int foo(T[] var)
    {
        return 0;
    }
    public static int foo(List<String>[] var)
    {
        return 0;
    }
    
}
