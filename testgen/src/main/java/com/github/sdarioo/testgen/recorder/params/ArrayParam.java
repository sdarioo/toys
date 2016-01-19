package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.Collection;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

public class ArrayParam
    implements IParameter
{
    private final int _length;
    private final Class<?> _clazz;
    private final IParameter[] _values;
    
    ArrayParam(Object array)
    {
        _length = Array.getLength(array);
        _clazz = array.getClass();
        
        if (_length > Configuration.getDefault().getMaxArraySize()) {
            _values = new IParameter[0];
            return;
        }
        
        _values = new IParameter[_length];
        for (int i = 0; i < _length; i++) {
            Object element = Array.get(array, i);
            _values[i] = ParamsFactory.newValue(element);
        }
    }

    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        int maxSize = Configuration.getDefault().getMaxArraySize();
        if (_length > maxSize) {
            errors.add(MessageFormat.format("Array length exceeds maximum permitted size. Max={0}, length={1}.", //$NON-NLS-1$
                    maxSize, _length));
            return false;
        }
        return ParamsUtil.isSupported(_values, errors);
    }

    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        StringBuilder sb = new StringBuilder();
        for (IParameter param : _values) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(param.toSouceCode(builder));
        }
        return "new " + builder.getTypeName(_clazz) + '{' + sb.toString() + '}';
    }
    
    @Override
    public int hashCode() 
    {
        return ParamsUtil.hashCode(_values);
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ArrayParam)) {
            return false;
        }
        ArrayParam other = (ArrayParam)obj;
        return ParamsUtil.equals(_values, other._values);
    }

}
