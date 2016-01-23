package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class StringWrapperParamTest 
{
    @Test
    public void isStringWrapper()
    {
        assertFalse(StringWrapperParam.isStringWrapper(this));
        
        assertTrue(StringWrapperParam.isStringWrapper(new Wrapper1("")));
        assertTrue(StringWrapperParam.isStringWrapper(new Wrapper2()));
        assertFalse(StringWrapperParam.isStringWrapper(new Wrapper22()));
        assertTrue(StringWrapperParam.isStringWrapper(new Wrapper3()));
    }
    
    @Test
    public void testEquals()
    {
        assertEquals(new StringWrapperParam(new Wrapper1("")), new StringWrapperParam(new Wrapper1("")));
        assertEquals(new StringWrapperParam(Wrapper2.fromString("a")), new StringWrapperParam(Wrapper2.fromString("a")));
        
        assertNotEquals(new StringWrapperParam(new Wrapper1("a")), new StringWrapperParam(new Wrapper1("b")));
        assertNotEquals(new StringWrapperParam(new Wrapper1("a")), new StringWrapperParam(Wrapper2.fromString("a")));
    }
    
    @Test
    public void toSourceCode()
    {
        StringWrapperParam p1 = new StringWrapperParam(new Wrapper1("x"));
        StringWrapperParam p2 = new StringWrapperParam(Wrapper2.fromString("x"));
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        assertEquals("new StringWrapperParamTest.Wrapper1(\"x\")", p1.toSouceCode(builder));
        assertEquals("StringWrapperParamTest.Wrapper2.fromString(\"x\")", p2.toSouceCode(builder));
    }
    
    public static class Wrapper1
    {
        String s;
        public Wrapper1(String s) { this.s = s;}
        @Override
        public String toString() {
            return s;
        }
    }
    public static class Wrapper2
    {
        String s;
        public static Wrapper2 fromString(String s) { 
            Wrapper2 w = new Wrapper2();
            w.s = s;
            return w;
        }
        @Override
        public String toString() {
            return s;
        }
    }
    public static class Wrapper22
    {
        public static Wrapper2 fromString(String s) { return null;}
    }
    public static class Wrapper3
    {
        public static Wrapper3 valueOf(String s) { return null;}
        @Override
        public String toString() {
            return "Wrapper3";
        }
    }
}
