package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Array;
import java.util.Collection;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

public class ArrayParam
    implements IParameter
{
    private final int _length;
    private final Class<?> _clazz;
    private final IParameter[] _array;
    
    ArrayParam(Object array)
    {
        _length = Array.getLength(array);
        _clazz = array.getClass();
        
        if (_length > Configuration.getDefault().getMaxArraySize()) {
            _array = new IParameter[0];
            return;
        }
        
        _array = new IParameter[_length];
        for (int i = 0; i < _length; i++) {
            Object element = Array.get(array, i);
            _array[i] = ParamsFactory.newValue(element);
        }
    }

    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        if (_length > Configuration.getDefault().getMaxArraySize()) {
            return false;
        }
        boolean bValid = true;
        for (IParameter param : _array) {
            bValid &= param.isSupported(errors);
        }
        return bValid;
    }

    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        StringBuilder sb = new StringBuilder();
        for (IParameter param : _array) {
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
        int hash = 1;
        for (IParameter param : _array) {
            hash = 31*hash + param.hashCode();
        }
        return hash;
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
        if (_length != other._length) {
            return false;
        }
        for (int i = 0; i < _length; i++) {
            if (!_array[i].equals(other._array[i])) {
                return false;
            }
        }
        return true;
    }

}
