

package com.github.sdarioo.testgen.recorder.params;

import java.util.Properties;

import com.github.sdarioo.testgen.recorder.IParameter;

public final class ParamsFactory 
{
    private ParamsFactory() {}

    // PRIMITIVE TYPES
    public static IParameter newValue(boolean b)
    {
        return null; // TODO
    }
    public static IParameter newValue(char c)
    {
        return null; // TODO
    }
    public static IParameter newValue(byte b)
    {
        return null; // TODO
    }
    public static IParameter newValue(short s)
    {
        return null; // TODO
    }
    public static IParameter newValue(int i)
    {
        return new IntParam(i);
    }
    public static IParameter newValue(float f)
    {
        return null; // TODO
    }
    public static IParameter newValue(long l)
    {
        return null; // TODO
    }
    public static IParameter newValue(double d)
    {
        return null; // TODO
    }
    
    
    // OBJECT TYPES
    public static IParameter newValue(Object value)
    {
        if (value == null) {
            return IParameter.NULL;
        }
        if (value instanceof String) {
            return new StringParam((String)value);
        }
        if (value instanceof Properties) {
            return new PropertiesParam((Properties)value);
        }
        return IParameter.UNKNOWN;
    }
}
