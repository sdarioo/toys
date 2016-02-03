/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.util.Collection;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

public class UnknownParam
    extends AbstractParam
{
    
    public UnknownParam(Class<?> clazz)
    {
        super(clazz, null);
    }
    
    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        errors.add("Unsupported type: " + getRecordedType().getName()); //$NON-NLS-1$
        return false;
    }

    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        return IParameter.NULL.toSouceCode(builder);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof UnknownParam)) {
            return false;
        }
        UnknownParam other = (UnknownParam)obj;
        return getRecordedType().equals(other.getRecordedType());
    }
    
    @Override
    public int hashCode() 
    {
        return getRecordedType().hashCode();
    }
    
}
