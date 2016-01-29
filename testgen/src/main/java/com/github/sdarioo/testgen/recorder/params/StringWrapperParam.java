package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Objects;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

/**
 * Represents classes that has constructor with single String argument or static factory method:
 * - fromString(String)
 * - valueOf(String)
 */
public class StringWrapperParam
    implements IParameter
{
    private final Class<?> _clazz;
    private final StringParam _stringParam;
    
    private static final String FROM_STRING = "fromString"; //$NON-NLS-1$
    private static final String VALUE_OF = "valueOf"; //$NON-NLS-1$
    
    public static boolean isStringWrapper(Object value)
    {
        Class<?> clazz = value.getClass();
        return (getFactoryMethod(clazz, FROM_STRING, value.getClass()) != null) ||
               (getFactoryMethod(clazz, VALUE_OF, value.getClass()) != null);
    }
    
    public StringWrapperParam(Object value)
    {
        _clazz = value.getClass();
        _stringParam = new StringParam(value.toString());
    }

    @Override
    public boolean isSupported(Collection<String> errors)
    {
        return _stringParam.isSupported(errors);
    }

    @Override
    public String toSouceCode(TestSuiteBuilder builder)
    {
        String typeName = builder.getTypeName(_clazz);
        String str = _stringParam.toSouceCode(builder);
        
        Method method = getFactoryMethod(_clazz, FROM_STRING, _clazz);
        if (method == null) {
            method = getFactoryMethod(_clazz, VALUE_OF, _clazz);
            if (method == null) {
                return IParameter.NULL.toSouceCode(builder);
            }
        }
        return MessageFormat.format("{0}.{1}({2})", typeName, method.getName(), str); //$NON-NLS-1$
    }
    
    @Override
    public int hashCode() 
    {
        return Objects.hash(_clazz, _stringParam);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StringWrapperParam)) {
            return false;
        }
        StringWrapperParam other = (StringWrapperParam)obj;
        return _clazz.equals(other._clazz) && _stringParam.equals(other._stringParam);
    }
    
//    private static Constructor<?> getConstructor(Class<?> clazz)
//    {
//        try {
//            return clazz.getConstructor(String.class);
//        } catch (NoSuchMethodException | SecurityException e) {
//            return null;
//        }
//    }
    
    private static Method getFactoryMethod(Class<?> clazz, String name, Class<?> returnType)
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
