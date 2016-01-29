/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ListParam
    extends CollectionParam
{
    
    public ListParam(List<?> list)
    {
        this(list, null);
    }
    
    public ListParam(java.util.List<?> list, Type listGenericType)
    {
        super(list, listGenericType);
    }
    
    @Override
    public boolean isSupported(Collection<String> errors) 
    {
       // toSourceCode output generates List so it must be compatible with generic type if provided
        Type listGenericType = getGenericType();
        Class<?> listType = ParamsUtil.getRawType(listGenericType);
        if ((listType != null) && !listType.isAssignableFrom(List.class)) {
            errors.add("Unsupported list type: " + listType.getName()); //$NON-NLS-1$
            return false;
        }
        
        return super.isSupported(errors);
    }

    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        builder.addImport(Arrays.class.getName());
        
        Type elementType = getElementType();
        String elementTypeName = builder.getGenericTypeName(elementType);
        String elementTypeSpec = (elementTypeName != null) ? ('<' + elementTypeName + '>') : "";
        
        return MessageFormat.format(TEMPLATE, elementTypeSpec, getValuesSourceCode(builder));
    }
    
    private static final String TEMPLATE = "Arrays.{0}asList({1})"; //$NON-NLS-1$
}
