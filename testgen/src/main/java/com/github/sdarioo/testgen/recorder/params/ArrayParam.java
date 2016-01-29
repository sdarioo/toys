package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ArrayParam
    extends CollectionParam
{
    private final Class<?> _componentType;
    
    ArrayParam(Object array)
    {
        this(array, null);
    }
    
    ArrayParam(Object array, Type genericArrayType)
    {
        super(asList(array), genericArrayType);
        
        Class<?> componentType = null;
        if (genericArrayType instanceof GenericArrayType) {
            componentType = ParamsUtil.getRawType(((GenericArrayType)genericArrayType).getGenericComponentType());
        } else if (genericArrayType instanceof Class<?>) {
            componentType = ((Class<?>)genericArrayType).getComponentType();
        }
        if (componentType == null) {
            componentType = array.getClass().getComponentType();
        }
        _componentType = componentType;
    }

    private static List<?> asList(Object array)
    {
        List<Object> list = new ArrayList<Object>();
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            list.add(Array.get(array, i));
        }
        return list;
    }

    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        return MessageFormat.format(TEMPLATE, 
                builder.getTypeName(_componentType),
                getValuesSourceCode(builder));
    }

    private static final String TEMPLATE = "new {0}[]'{'{1}'}'"; //$NON-NLS-1$
}
