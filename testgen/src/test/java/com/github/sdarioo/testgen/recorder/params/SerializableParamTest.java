package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class SerializableParamTest 
{
    @Test
    public void testEquals() throws Exception
    {
        assertEquals(new SerializableParam(new Inner1("x")), new SerializableParam(new Inner1("x")));
        assertNotEquals(new SerializableParam(new Inner1("x")), new SerializableParam(new Inner1("y")));
        assertNotEquals(new SerializableParam(new Inner1("x")), new SerializableParam(new Inner2("x")));
    }
    
    @Test
    public void toSourceCode()
    {
        SerializableParam p = new SerializableParam(new Inner1("x"));
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("deserialize(\"res/SerializableParamTest.Inner1\")", p.toSouceCode(Inner1.class, builder));
    }
    
    
    public static class Inner1 implements Serializable 
    {
        private String x;
        
        public Inner1(String s) {
            this.x = s;
        }
    }
    public static class Inner2 implements Serializable 
    {
        private String x;
        
        public Inner2(String s) {
            this.x = s;
        }
    }
}
