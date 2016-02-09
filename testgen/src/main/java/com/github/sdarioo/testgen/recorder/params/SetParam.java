/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;


import java.lang.reflect.Type;
import java.util.*;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.IParameter;

public class SetParam
    extends CollectionParam
{
    
    public SetParam(Set<?> set) 
    {
        this(set, null);
    }
    
    public SetParam(Set<?> set, Type setGenericType)
    {
        super(set, new HashSet<IParameter>(), setGenericType);
    }
    
    @Override
    protected Class<?> getGeneratedSourceCodeType() 
    {
        return Set.class;
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        String elements = getElementsSourceCode(builder);
        if (elements.length() <= 0) {
            builder.addImport(Collections.class.getName());
            return fmt("Collections.{0}emptySet()", getElementTypeSpec(builder));
        }
        
        builder.addImport(Arrays.class.getName());
        builder.addImport(Set.class.getName());
        
        TestMethod asSet = builder.addHelperMethod(AS_SET_TEMPLATE, "asSet");
        return fmt("{0}({1})", asSet.getName(), elements);
    }

    @SuppressWarnings("nls")
    private static final String AS_SET_TEMPLATE = 
            "private static <T> Set<T> {0}(T... elements)  '{'\n"+
            "    return new HashSet<T>(Arrays.<T>asList(elements));\n"+
            "'}'\n";
    
}
