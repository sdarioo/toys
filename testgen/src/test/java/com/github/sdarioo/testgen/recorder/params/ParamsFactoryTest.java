/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

public class ParamsFactoryTest
{
    @SuppressWarnings("nls")
    @Test
    public void testPrimitiveTypes()
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        IParameter p = ParamsFactory.newValue(true);
        assertEquals("true", p.toSouceCode(builder));
        
        p = ParamsFactory.newValue(Character.valueOf('x').charValue());
        assertEquals("'x'", p.toSouceCode(builder));
        
        p = ParamsFactory.newValue(Character.valueOf((char)1).charValue());
        assertEquals("(char)1", p.toSouceCode(builder));
        
        byte b = 127;
        p = ParamsFactory.newValue(Byte.valueOf(b).byteValue());
        assertEquals("(byte)127", p.toSouceCode(builder));
        b = -127;
        p = ParamsFactory.newValue(Byte.valueOf(b).byteValue());
        assertEquals("(byte)-127", p.toSouceCode(builder));
        
        short s = 1000;
        p = ParamsFactory.newValue(Short.valueOf(s).shortValue());
        assertEquals("(short)1000", p.toSouceCode(builder));
        s = -1000;
        p = ParamsFactory.newValue(Short.valueOf(s).shortValue());
        assertEquals("(short)-1000", p.toSouceCode(builder));
        
        int i = 100000;
        p = ParamsFactory.newValue(Integer.valueOf(i).intValue());
        assertEquals("100000", p.toSouceCode(builder));
        i = -100000;
        p = ParamsFactory.newValue(Integer.valueOf(i).intValue());
        assertEquals("-100000", p.toSouceCode(builder));
        
        long l = Long.MAX_VALUE;
        p = ParamsFactory.newValue(Long.valueOf(l).longValue());
        assertEquals("9223372036854775807L", p.toSouceCode(builder));
        l = Long.MIN_VALUE;
        p = ParamsFactory.newValue(Long.valueOf(l).longValue());
        assertEquals("-9223372036854775808L", p.toSouceCode(builder));
        
        p = ParamsFactory.newValue(Float.valueOf(0.0f).floatValue());
        assertEquals("0.0f", p.toSouceCode(builder));
        
        p = ParamsFactory.newValue(Double.valueOf(0.1d).doubleValue());
        assertEquals("0.1d", p.toSouceCode(builder));
    }
    
    
    @SuppressWarnings("nls")
    @Test
    public void testPrimitiveWrappers()
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        
        IParameter p = ParamsFactory.newValue(Boolean.TRUE);
        assertEquals("true", p.toSouceCode(builder));
        
        p = ParamsFactory.newValue(Character.valueOf('x'));
        assertEquals("'x'", p.toSouceCode(builder));
        
        byte b = 127;
        p = ParamsFactory.newValue(Byte.valueOf(b));
        assertEquals("(byte)127", p.toSouceCode(builder));
        
        short s = 1000;
        p = ParamsFactory.newValue(Short.valueOf(s));
        assertEquals("(short)1000", p.toSouceCode(builder));
        
        int i = 100000;
        p = ParamsFactory.newValue(Integer.valueOf(i));
        assertEquals("100000", p.toSouceCode(builder));
        
        long l = Long.MAX_VALUE;
        p = ParamsFactory.newValue(Long.valueOf(l));
        assertEquals("9223372036854775807L", p.toSouceCode(builder));
        
        p = ParamsFactory.newValue(Float.valueOf(0.0f));
        assertEquals("0.0f", p.toSouceCode(builder));
        
        p = ParamsFactory.newValue(Double.valueOf(0.1d));
        assertEquals("0.1d", p.toSouceCode(builder));
    }
}
