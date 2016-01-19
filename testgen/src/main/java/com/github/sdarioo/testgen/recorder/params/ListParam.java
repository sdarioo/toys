/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.text.MessageFormat;
import java.util.Collection;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

public class ListParam
    implements IParameter
{
    private final int _originalSize;
    private final IParameter[] _values;
    
    ListParam(java.util.List<?> list)
    {
        _originalSize = list.size();
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            _values = new IParameter[0];
            return;
        }
        
        _values = new IParameter[_originalSize];
        int i = 0;
        for (Object object : list) {
            IParameter value = ParamsFactory.newValue(object);
            _values[i++] = value;
        }
    }

    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        int maxSize = Configuration.getDefault().getMaxArraySize();
        if (_originalSize > maxSize) {
            errors.add(MessageFormat.format("List size exceeds maximum permitted size. Max={0}, size={1}.", //$NON-NLS-1$
                    maxSize, _originalSize));
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
        builder.addImport("java.util.Arrays");
        return "Arrays.asList(" + sb.toString() + ')';
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
        if (!(obj instanceof ListParam)) {
            return false;
        }
        ListParam other = (ListParam)obj;
        return ParamsUtil.equals(_values, other._values);
    }
}
