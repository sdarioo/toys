/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestClass;

public class StringParamTest 
{
    @SuppressWarnings("nls")
    @Test
    public void testSourceCode()
    {
        StringParam v = new StringParam("");
        assertEquals("\"\"", v.toSouceCode(String.class, new TestSuiteBuilder()));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void shouldEscapeText()
    {
        StringParam p = new StringParam("c:\\win\\path");
        assertEquals("\"c:\\\\win\\\\path\"", p.toSouceCode(String.class, new TestSuiteBuilder()));
        
        p = new StringParam("line1\n\rline2");
        assertEquals("\"line1\\n\\rline2\"", p.toSouceCode(String.class, new TestSuiteBuilder()));
    }
    
    @Test
    public void testCountLines()
    {
        assertEquals(1, StringParam.getLinesCount(""));
        assertEquals(1, StringParam.getLinesCount("text"));
        assertEquals(2, StringParam.getLinesCount("\n"));
        assertEquals(3, StringParam.getLinesCount("\n\n"));
        assertEquals(3, StringParam.getLinesCount("1\n2\n3"));
    }
    
    @Test
    public void testCreateResource()
    {
        StringBuilder sb = new StringBuilder();
        int maxStringLength = Configuration.getDefault().getMaxStringLength();
        for (int i = 0; i< maxStringLength; i++) {
            sb.append("XX");
        }
        StringParam p = new StringParam(sb.toString());
        TestSuiteBuilder builder = new TestSuiteBuilder();
        String code = p.toSouceCode(String.class, builder);
        assertTrue(code.length() < maxStringLength);
        
        TestClass test = builder.buildTestClass();
        assertEquals(1, test.getResources().size());
        assertEquals(sb.toString(), test.getResources().get(0).getContent());
    }
}

