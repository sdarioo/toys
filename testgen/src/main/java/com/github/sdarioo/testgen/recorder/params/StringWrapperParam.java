package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

/**
 * Represents classes that has constructor with single String argument or static factory method:
 * - fromString(String)
 * - valueOf(String)
 */
public class StringWrapperParam
    extends AbstractParam
{
    private final StringParam _stringParam;
    private final String _factoryMethod;

    
    public StringWrapperParam(Object value, String factoryMethod)
    {
        this(value, factoryMethod, null);
    }
    
    public StringWrapperParam(Object value, String factoryMethod, Type paramType)
    {
        super(value.getClass(), paramType);
        
        _factoryMethod = factoryMethod;
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
        
        
        return fmt("{0}.{1}({2})", typeName, _factoryMethod, str); //$NON-NLS-1$
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
    

    
}
