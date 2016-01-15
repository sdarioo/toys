/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestSuiteBuilderTest 
{
    @SuppressWarnings("nls")
    @Test
    public void testGetSimpleName()
    {
        TestSuiteBuilder b = new TestSuiteBuilder();
        assertEquals("TestSuiteBuilderTest", b.getTypeName(TestSuiteBuilderTest.class));
        assertEquals("String[]", b.getTypeName(new String[0].getClass()));
        assertEquals("int[]", b.getTypeName(new int[0].getClass()));
        assertEquals("Inner", b.getTypeName(Inner.class));
        
        assertEquals(3, b.getImports().size());
        assertTrue(b.getImports().contains("com.github.sdarioo.testgen.generator.TestSuiteBuilderTest"));
        assertTrue(b.getImports().contains("java.lang.String"));
        assertTrue(b.getImports().contains("com.github.sdarioo.testgen.generator.TestSuiteBuilderTest.Inner"));
        
        
        b = new TestSuiteBuilder();
        assertEquals("TestSuiteBuilderTest", b.getTypeName(TestSuiteBuilderTest.class.getCanonicalName()));
        assertEquals("String[]", b.getTypeName(new String[0].getClass().getCanonicalName()));
        assertEquals("int[]", b.getTypeName(new int[0].getClass().getCanonicalName()));
        assertEquals("Inner", b.getTypeName(Inner.class.getCanonicalName()));
        
        assertEquals(3, b.getImports().size());
        assertTrue(b.getImports().contains("com.github.sdarioo.testgen.generator.TestSuiteBuilderTest"));
        assertTrue(b.getImports().contains("java.lang.String"));
        assertTrue(b.getImports().contains("com.github.sdarioo.testgen.generator.TestSuiteBuilderTest.Inner"));
    }
    
    public static class Inner {}
}
