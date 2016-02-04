/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;

import com.github.sdarioo.testgen.recorder.IParameter;

public abstract class AbstractParam
    implements IParameter
{
    private final Class<?> _class;
    private final Type _paramType;
    
    protected AbstractParam(Class<?> recordedType, Type paramType)
    {
        _class = recordedType;
        _paramType = paramType;
    }
    
    @Override
    public Class<?> getRecordedType() 
    {
        return _class;
    }
    
    @Override
    public Type getType() 
    {
        return _paramType;
    }
    
    /**
     * If this parameter type represents parameterized generic type than return array
     * of actual type arguments. Otheriwse returns empty array
     * @return
     */
    protected Type[] getActualTypeArguments()
    {
        if (_paramType instanceof ParameterizedType) {
            return ((ParameterizedType)_paramType).getActualTypeArguments();
        }
        return new Type[0];
    }
    
    
    protected static String fmt(String pattern, Object... args)
    {
        return MessageFormat.format(pattern, args);
    }
}
