/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

public class IntParam 
    implements IParameter
{
    private final int _value;
    
    public IntParam(int value)
    {
        _value = value;
    }
    
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        return String.valueOf(_value);
    }
    
    @Override
    public String toString() 
    {
        return String.valueOf(_value);
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof IntParam)) {
            return false;
        }
        IntParam other = (IntParam)obj;
        return _value == other._value;
    }
    
    @Override
    public int hashCode() 
    {
        return _value;
    }
}
