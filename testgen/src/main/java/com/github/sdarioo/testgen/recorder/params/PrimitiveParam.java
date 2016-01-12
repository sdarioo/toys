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

public class PrimitiveParam implements IParameter
{
    private final String _sourceCode;
    
    PrimitiveParam(String sourceCode)
    {
        _sourceCode = sourceCode;
    }
    
    @Override
    public boolean isValid(Collection<String> errors) 
    {
        return true;
    }
    
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        return _sourceCode;
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof PrimitiveParam)) {
            return false;
        }
        PrimitiveParam other = (PrimitiveParam)obj;
        return _sourceCode.equals(other._sourceCode);
    }
    
    @Override
    public int hashCode() 
    {
        return _sourceCode.hashCode();
    }
}
