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
    extends AbstractParam
{
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
        super(value.getClass(), null);
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
        Class<?> clazz = getRecordedType();
        
        String typeName = builder.getTypeName(clazz);
        String str = _stringParam.toSouceCode(builder);
        
        Method method = getFactoryMethod(clazz, FROM_STRING, clazz);
        if (method == null) {
            method = getFactoryMethod(clazz, VALUE_OF, clazz);
            if (method == null) {
                return IParameter.NULL.toSouceCode(builder);
            }
        }
        return MessageFormat.format("{0}.{1}({2})", typeName, method.getName(), str); //$NON-NLS-1$
    }
    
    @Override
    public int hashCode() 
    {
        return Objects.hash(getRecordedType(), _stringParam);
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
        return getRecordedType().equals(other.getRecordedType()) && _stringParam.equals(other._stringParam);
    }
    
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
