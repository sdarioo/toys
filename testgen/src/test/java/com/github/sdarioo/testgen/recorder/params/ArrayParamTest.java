package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

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
        assertEquals("new Properties[]{}", p.toSouceCode(builder));
    }
    
    @Test
    public void testIntArray()
    {
        ArrayParam p = new ArrayParam(new int[] {1, 2, 3});
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new int[]{1, 2, 3}", p.toSouceCode(builder));
    }
    
    @Test
    public void testStringArray()
    {
        ArrayParam p = new ArrayParam(new String[] {"ala", "ma", "kota"});
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new String[]{\"ala\", \"ma\", \"kota\"}", p.toSouceCode(builder));
    }
    
    @Test
    public void testTwoDimArray()
    {
        ArrayParam p = new ArrayParam(new String[][] { new String[]{"ala"}, new String[]{"ma"}});
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("new String[][]{new String[]{\"ala\"}, new String[]{\"ma\"}}", p.toSouceCode(builder));
    }
}
