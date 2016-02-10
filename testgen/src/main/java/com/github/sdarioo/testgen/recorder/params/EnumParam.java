package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Type;
import java.util.Collection;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

public class EnumParam
    extends AbstractParam
{
    private final String _name;
    
    EnumParam(Enum<?> e)
    {
        super(e.getClass());
        _name = e.toString();
    }
    
    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        return true;
    }

    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        String enumClass = builder.getTypeName(getRecordedType());
        return enumClass + '.' + _name;
    }
    
    @Override
    public int hashCode() 
    {
        return getRecordedType().hashCode() + 31 * _name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof EnumParam)) {
            return false;
        }
        EnumParam other = (EnumParam)obj;
        return getRecordedType().equals(other.getRecordedType()) && _name.equals(other._name);
    }
}
