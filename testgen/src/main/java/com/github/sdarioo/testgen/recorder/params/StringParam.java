/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import org.apache.commons.lang3.StringEscapeUtils;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

public class StringParam
    implements IParameter
{
    private final String _value;
    
    /**
     * @param value string value, never null
     * @pre value != null
     */
    public StringParam(String value)
    {
        _value = value;
    }
    
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        // TODO - multiline text as separate resource file?
        
        return '\"' + StringEscapeUtils.escapeJava(_value) + '\"';
    }
    
    @Override
    public String toString() 
    {
        return _value;
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof StringParam)) {
            return false;
        }
        StringParam other = (StringParam)obj;
        return (_value != null) ? _value.equals(other._value) : (other._value == null);
    }
    
    @Override
    public int hashCode() 
    {
        return (_value != null) ? _value.hashCode() : 0;
    }
}
