/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

public class ListParam
    extends CollectionParam
{
    public ListParam(List<?> list)
    {
        this(list, null);
    }
    
    public ListParam(List<?> list, Type listGenericType)
    {
        super(list, new ArrayList<IParameter>(), listGenericType);
    }
    
    @Override
    protected Class<?> getGeneratedSourceCodeType() 
    {
        return List.class;
    }

    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        builder.addImport(Arrays.class.getName());
        builder.addImport(ArrayList.class.getName());
        
        String elements = getElementsSourceCode(builder);
        if (elements.length() > 0) {
            return MessageFormat.format("Arrays.{0}asList({1})", getElementTypeSpec(builder), elements);
        } else {
            return MessageFormat.format("new ArrayList{0}()", getElementTypeSpec(builder));
        }
    }

}
