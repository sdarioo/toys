/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class StringParamTest 
{
    @SuppressWarnings("nls")
    @Test
    public void testSourceCode()
    {
        StringParam v = new StringParam("");
        assertEquals("\"\"", v.toSouceCode(new TestSuiteBuilder()));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void shouldEscapeText()
    {
        StringParam v = new StringParam("c:\\win\\path");
        assertEquals("\"c:\\\\win\\\\path\"", v.toSouceCode(new TestSuiteBuilder()));
    }
}
