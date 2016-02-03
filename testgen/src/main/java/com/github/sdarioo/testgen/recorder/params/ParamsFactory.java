

package com.github.sdarioo.testgen.recorder.params;

import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Properties;

import org.apache.commons.lang3.ClassUtils;

import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.recorder.params.beans.Bean;
import com.github.sdarioo.testgen.recorder.params.beans.BeanFactory;


public final class ParamsFactory 
{
    private ParamsFactory() {}

    
    public static IParameter newValue(Object value)
    {
        return newValue(value, null);
    }

    public static IParameter newValue(Object value, Type paramType)
    {
        if (value == null) {
            return IParameter.NULL;
        }
        Class<?> clazz = value.getClass();
        
        if (value instanceof String) {
            return new StringParam((String)value);
        }
        if (clazz.isArray()) {
            return new ArrayParam(value, paramType);
        }
        if (clazz.isEnum()) {
            return new EnumParam((Enum<?>)value);
        }
        if (ClassUtils.isPrimitiveWrapper(value.getClass())) {
            return toPrimitiveValue(value, paramType);
        }
        
        // Collections
        if (value instanceof Properties) {
            return new PropertiesParam((Properties)value);
        }
        if (value instanceof java.util.List<?>) {
            return new ListParam((java.util.List<?>)value, paramType);
        }
        if (value instanceof java.util.Set<?>) {
            return new SetParam((java.util.Set<?>)value, paramType);
        }
        if (value instanceof java.util.Map<?,?>) {
            return new MapParam((java.util.Map<?,?>)value, paramType);
        }
        
        // Java Bean
        Bean bean = BeanFactory.getInstance().getBean(clazz);
        if (bean != null) {
            return new BeanParam(value, bean, paramType);
        }
        // Class with fromString or valueOf factory methods
        if (StringWrapperParam.isStringWrapper(value)) {
            return new StringWrapperParam(value);
        }
        // Serializable class
        if (value instanceof Serializable) {
            return new SerializableParam((Serializable)value);
        }
        
        return new UnknownParam(value.getClass());
    }
        
    private static IParameter toPrimitiveValue(Object value, Type paramType)
    {
        String str = null;
        if (value instanceof Boolean) {
            str = value.toString();
        } else if (value instanceof Byte) {
            str = "(byte)" + value.toString(); //$NON-NLS-1$
        } else if (value instanceof Character) {
            char c = ((Character)value).charValue();
            str = isPrintableChar(c) ?  ('\'' + String.valueOf(c) + '\'') : "(char)" + Integer.toString(c); //$NON-NLS-1$
        } else if (value instanceof Short) {
            str = "(short)" + value.toString(); //$NON-NLS-1$
        } if (value instanceof Integer) {
            str = value.toString();
        } if (value instanceof Long) {
            str = value.toString() + 'L';
        } if (value instanceof Double) {
            str = value.toString() + 'd';
        } else if (value instanceof Float) {
            str = value.toString() + 'f';
        }
        Class<?> type = ParamsUtil.getRawType(paramType);
        return (str != null) ? new PrimitiveParam(str, type) : new UnknownParam(value.getClass());
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
