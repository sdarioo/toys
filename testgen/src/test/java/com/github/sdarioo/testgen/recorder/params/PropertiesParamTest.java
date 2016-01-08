/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class PropertiesParamTest 
{
    @SuppressWarnings("nls")
    @Test
    public void testToSourceCode()
    {
        Properties p = new Properties();
        p.setProperty("key1", "value1");
        p.setProperty("key2", "value2");
        
        PropertiesParam param = new PropertiesParam(p);
        
        String text = param.toSouceCode(new TestSuiteBuilder());
        
        assertEquals("props(pair(\"key2\", \"value2\"), pair(\"key1\", \"value1\"))", text);
    }
}
