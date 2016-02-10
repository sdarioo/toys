/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Collection;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.util.TypeUtil;

public abstract class AbstractParam
    implements IParameter
{
    private final Class<?> _recordedType;
    
    protected AbstractParam(Class<?> recordedType)
    {
        _recordedType = recordedType;
    }
    
    @Override
    public Class<?> getRecordedType() 
    {
        return _recordedType;
    }

    protected static boolean isAssignable(Type type, Type targetType, Collection<String> errors)
    {
        if (!org.apache.commons.lang3.reflect.TypeUtils.isAssignable(type, targetType)) {
            errors.add("Unsupported type: " + TypeUtil.getName(targetType, new TestSuiteBuilder())); //$NON-NLS-1$
            return false;
        }
        return true;
    }
    
    protected static String fmt(String pattern, Object... args)
    {
        return MessageFormat.format(pattern, args);
    }
    
}
