

package com.github.sdarioo.testgen.recorder.params;

import java.awt.event.KeyEvent;
import java.util.Properties;

import org.apache.commons.lang3.ClassUtils;

import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.recorder.params.beans.Bean;
import com.github.sdarioo.testgen.recorder.params.beans.BeanFactory;

// TODO
// - constructor with single String argument
// - factory method fromString or valueOf
public final class ParamsFactory 
{
    private ParamsFactory() {}

    // PRIMITIVE TYPES
    public static IParameter newValue(boolean b)
    {
        return new PrimitiveParam(b ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
    }
    public static IParameter newValue(char c)
    {
        return isPrintableChar(c) ?
                new PrimitiveParam('\'' + String.valueOf(c) + '\'') :
                new PrimitiveParam("(char)"+Integer.toString(c)); //$NON-NLS-1$
    }
    public static IParameter newValue(byte b)
    {
        return new PrimitiveParam("(byte)"+String.valueOf(b)); //$NON-NLS-1$
    }
    public static IParameter newValue(short s)
    {
        return new PrimitiveParam("(short)"+String.valueOf(s)); //$NON-NLS-1$
    }
    public static IParameter newValue(int i)
    {
        return new PrimitiveParam(String.valueOf(i));
    }
    public static IParameter newValue(float f)
    {
        return new PrimitiveParam(String.valueOf(f) + 'f');
    }
    public static IParameter newValue(long l)
    {
        return new PrimitiveParam(String.valueOf(l) + 'L');
    }
    public static IParameter newValue(double d)
    {
        return new PrimitiveParam(String.valueOf(d) + 'd');
    }
    
    
    // OBJECT TYPES
    public static IParameter newValue(Object value)
    {
        if (value == null) {
            return IParameter.NULL;
        }
        Class<?> clazz = value.getClass();
        
        if (value instanceof String) {
            return new StringParam((String)value);
        }
        if (clazz.isArray()) {
            return new ArrayParam(value);
        }
        if (clazz.isEnum()) {
            return new EnumParam((Enum<?>)value);
        }
        if (ClassUtils.isPrimitiveWrapper(value.getClass())) {
            return toPrimitiveValue(value);
        }
        if (value instanceof java.util.List<?>) {
            return new ListParam((java.util.List<?>)value);
        }
        if (value instanceof Properties) {
            return new PropertiesParam((Properties)value);
        }
        Bean bean = BeanFactory.getInstance().getBean(clazz);
        if (bean != null) {
            return new BeanParam(value, bean);
        }
        
        return new UnknownParam(value.getClass());
    }
        
    private static IParameter toPrimitiveValue(Object value)
    {
        if (value instanceof Boolean) {
            return newValue(((Boolean)value).booleanValue());
        }
        if (value instanceof Byte) {
            return newValue(((Byte)value).byteValue());
        }
        if (value instanceof Character) {
            return newValue(((Character)value).charValue());
        }
        if (value instanceof Short) {
            return newValue(((Short)value).shortValue());
        }
        if (value instanceof Integer) {
            return newValue(((Integer)value).intValue());
        }
        if (value instanceof Long) {
            return newValue(((Long)value).longValue());
        }
        if (value instanceof Double) {
            return newValue(((Double)value).doubleValue());
        }
        if (value instanceof Float) {
            return newValue(((Float)value).floatValue());
        }
        return new UnknownParam(value.getClass());
    }
    
    
    public static boolean isPrintableChar(char c)
    {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }
}
