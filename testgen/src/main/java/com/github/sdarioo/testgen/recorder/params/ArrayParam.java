package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class ArrayParam
    extends CollectionParam
{
    private final Class<?> _arrayClazz;
    
    ArrayParam(Object array)
    {
        super(asList(array));
        _arrayClazz = array.getClass();
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

    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        String arrayType = builder.getTypeName(_arrayClazz); 
        return "new " + arrayType + '{' + getValuesSourceCode(builder) + '}';
    }

}
