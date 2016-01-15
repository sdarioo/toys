package com.github.sdarioo.testgen.recorder.params;

import java.util.Collection;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

public class EnumParam
    implements IParameter
{
    private final Class<?> _enumClass;
    private final String _name;
    
    EnumParam(Enum<?> e)
    {
        _enumClass = e.getClass();
        _name = e.toString();
    }

    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        return true;
    }

    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        String enumClass = builder.getTypeName(_enumClass);
        return enumClass + '.' + _name;
    }
    
    @Override
    public int hashCode() 
    {
        return _enumClass.hashCode() + 31 * _name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof EnumParam)) {
            return false;
        }
        EnumParam other = (EnumParam)obj;
        return _enumClass.equals(other._enumClass) && _name.equals(other._name);
    }
}
