

package com.github.sdarioo.testgen.recorder.params;

import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;

import org.apache.commons.lang3.ClassUtils;

import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.recorder.params.beans.Bean;
import com.github.sdarioo.testgen.recorder.params.beans.BeanFactory;
import com.github.sdarioo.testgen.recorder.params.beans.BeanParam;
import com.github.sdarioo.testgen.recorder.params.proxy.ProxyFactory;
import com.github.sdarioo.testgen.recorder.params.proxy.ProxyParam;


public final class ParamsFactory 
{
    private ParamsFactory() {}


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
        
        // Proxy
        if (ProxyFactory.isProxy(value)) {
            return new ProxyParam(value);
        }

        // Collections
        if (value instanceof Properties) {
            return new PropertiesParam((Properties)value);
        }
        if (value instanceof java.util.List<?>) {
            return new ListParam((java.util.List<?>)value);
        }
        if (value instanceof java.util.Set<?>) {
            return new SetParam((java.util.Set<?>)value);
        }
        if (value instanceof java.util.Map<?,?>) {
            return new MapParam((java.util.Map<?,?>)value);
        }
        // Class with fromString or valueOf factory methods
        String factoryMethod = getStaticFactoryMethodName(value);
        if (factoryMethod != null) {
            return new StringWrapperParam(value, factoryMethod);
        }
        // Java Bean
        Bean bean = BeanFactory.getInstance().getBean(clazz);
        if (bean != null) {
            return new BeanParam(value, bean);
        }
        // Serializable class
        if (value instanceof Serializable) {
            return new SerializableParam((Serializable)value);
        }
        
        return new UnknownParam(value.getClass());
    }
        
    private static IParameter toPrimitiveValue(Object value)
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
        Class<?> type = value.getClass();
        return (str != null) ? new PrimitiveParam(str, type) : new UnknownParam(type);
    }    
    
    public static boolean isPrintableChar(char c)
    {
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
        return (!Character.isISOControl(c)) &&
                c != KeyEvent.CHAR_UNDEFINED &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }
    
    
    private static String getStaticFactoryMethodName(Object value)
    {
        Class<?> clazz = value.getClass();
        Method method = getStaticMethod(clazz, "fromString", value.getClass()); //$NON-NLS-1$
        if (method == null) {
            method = getStaticMethod(clazz, "valueOf", value.getClass()); //$NON-NLS-1$
        }
        return method != null ? method.getName() : null;
    }
    
    private static Method getStaticMethod(Class<?> clazz, String name, Class<?> returnType)
    {
        try {
            Method method = clazz.getMethod(name, String.class);
            if (Modifier.isStatic(method.getModifiers()) &&
                    returnType.equals(method.getReturnType())) 
            {
                return method;
            }
        } catch (NoSuchMethodException | SecurityException e1) {}
        return null;
    }
    
}
