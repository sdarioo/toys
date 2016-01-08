

package com.github.sdarioo.testgen.recorder.params;

import java.util.Properties;

import com.github.sdarioo.testgen.recorder.IParameter;

public final class ParamsFactory 
{
    private ParamsFactory() {}
    
    public static IParameter newValue(int value)
    {
        return new IntParam(value);
    }
    
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
