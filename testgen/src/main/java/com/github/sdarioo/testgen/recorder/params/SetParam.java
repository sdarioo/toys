/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;


import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class SetParam
    extends CollectionParam
{
    
    public SetParam(Set<?> set) 
    {
        this(set, null);
    }
    
    public SetParam(Set<?> set, Type setGenericType)
    {
        super(set, setGenericType);
    }
    
    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        // toSourceCode output generates HashSet so it must be compatible with generic type if provided
        Type setGenericType = getGenericType();
        Class<?> setType = ParamsUtil.getRawType(setGenericType);
        if ((setType != null) && !setType.isAssignableFrom(HashSet.class)) {
            errors.add("Unsupported set type: " + setType.getName()); //$NON-NLS-1$
            return false;
        }
        
        return super.isSupported(errors);
    }
    
    
    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        builder.addImport(Arrays.class.getName());
        builder.addImport(HashSet.class.getName());
        
        Type elementType = getElementType();
        String elementTypeName = builder.getGenericTypeName(elementType);
        String elementTypeSpec = (elementTypeName != null) ? ('<' + elementTypeName + '>') : "";
        
        return MessageFormat.format(TEMPLATE, elementTypeSpec, getValuesSourceCode(builder));
    }

    private static final String TEMPLATE = "new HashSet{0}(Arrays.asList({1}))"; //$NON-NLS-1$
}
