/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ListParam
    extends CollectionParam
{
    
    public ListParam(java.util.List<?> list)
    {
        super(list);
    }

    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        String arrays = builder.getTypeName("java.util.Arrays");
        return arrays + ".asList(" + getValuesSourceCode(builder) + ')';
    }

}
