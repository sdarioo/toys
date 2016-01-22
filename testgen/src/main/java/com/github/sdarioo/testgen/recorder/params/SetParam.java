/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.text.MessageFormat;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class SetParam
    extends CollectionParam
{
    
    protected SetParam(java.util.Set<?> set) 
    {
        super(set);
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        String setImpl = builder.getTypeName("java.util.HashSet");
        String arrays = builder.getTypeName("java.util.Arrays");
        
        String template = "new {0}({1}.asList({2}))";
        return MessageFormat.format(template, setImpl, arrays, getValuesSourceCode(builder));
    }

}
