package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class StringWrapperParamTest 
{
    @Test
    public void isStringWrapper()
    {
        assertFalse(ParamsFactory.newValue(this) instanceof StringWrapperParam);
        assertFalse(ParamsFactory.newValue(new Wrapper1("")) instanceof StringWrapperParam);
        assertFalse(ParamsFactory.newValue(new Wrapper22()) instanceof StringWrapperParam);
        
        assertTrue(ParamsFactory.newValue(new Wrapper2()) instanceof StringWrapperParam);
        assertTrue(ParamsFactory.newValue(new Wrapper3()) instanceof StringWrapperParam);
    }
    
    @Test
    public void testEquals()
    {
        
        assertEquals(new StringWrapperParam(Wrapper2.fromString("a"), "fromString"),
                     new StringWrapperParam(Wrapper2.fromString("a"), "fromString"));
        
        assertNotEquals(new StringWrapperParam(Wrapper2.fromString("a"), "fromString"),
                new StringWrapperParam(Wrapper3.valueOf("a"), "valueOf"));
    }
    
    @Test
    public void toSourceCode()
    {
        StringWrapperParam p1 = new StringWrapperParam(Wrapper3.valueOf("x"), "valueOf");
        StringWrapperParam p2 = new StringWrapperParam(Wrapper2.fromString("x"), "fromString");
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        assertEquals("StringWrapperParamTest.Wrapper3.valueOf(\"x\")", p1.toSouceCode(Wrapper3.class, builder));
        assertEquals("StringWrapperParamTest.Wrapper2.fromString(\"x\")", p2.toSouceCode(Wrapper2.class, builder));
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
        String s;
        public static Wrapper3 valueOf(String s) { 
            Wrapper3 w = new Wrapper3();
            w.s = s;
            return w;
        }
        @Override
        public String toString() {
            return s;
        }
    }
}
