package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class EnumParamTest 
{
    @Test
    public void testEnum()
    {
        EnumParam p = new EnumParam(E.A);
        assertEquals("EnumParamTest.E.A", p.toSouceCode(E.class, new TestSuiteBuilder()));
        
        assertEquals(p, new EnumParam(E.A));
        assertNotEquals(p, new EnumParam(E.C));
    }
    
    public enum E
    {
        A,B,C
    }
}
