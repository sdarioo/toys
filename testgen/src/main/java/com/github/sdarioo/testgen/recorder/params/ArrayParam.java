package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

public class ArrayParam
    extends CollectionParam
{
    private final Class<?> _arrayType;
    private final Class<?> _componentType;
    
    ArrayParam(Object array)
    {
        this(array, null);
    }
    
    ArrayParam(Object array, Type genericArrayType)
    {
        super(asList(array), new ArrayList<IParameter>(), genericArrayType);
        
        _arrayType = array.getClass();
        Class<?> componentType = null;
        if (genericArrayType instanceof GenericArrayType) {
            componentType = ParamsUtil.getRawType(((GenericArrayType)genericArrayType).getGenericComponentType());
        } else if (genericArrayType instanceof Class<?>) {
            componentType = ((Class<?>)genericArrayType).getComponentType();
        }
        if (componentType == null) {
            componentType = _arrayType.getComponentType();
        }
        _componentType = componentType;
    }
    
    @Override
    public Class<?> getRecordedType() 
    {
        return _arrayType;
    }

    @Override
    protected Class<?> getGeneratedSourceCodeType() 
    {
        return _arrayType;
    }
    
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        return MessageFormat.format(TEMPLATE, 
                builder.getTypeName(_componentType),
                getElementsSourceCode(builder));
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


    private static final String TEMPLATE = "new {0}[]'{'{1}'}'"; //$NON-NLS-1$
}
