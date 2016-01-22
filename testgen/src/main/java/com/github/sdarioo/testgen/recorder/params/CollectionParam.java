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

public abstract class CollectionParam
    implements IParameter
{
    private final int _originalSize;
    protected final IParameter[] _values;
    
    protected CollectionParam(java.util.Collection<?> list)
    {
        _originalSize = list.size();
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            _values = new IParameter[0];
            return;
        }
        
        _values = new IParameter[_originalSize];
        int i = 0;
        for (Object obj : list) {
            _values[i] = ParamsFactory.newValue(obj);
            i++;
        }
    }
    
    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            errors.add(MessageFormat.format("Collection size exceeds maximum permitted size. Max={0}, size={1}.", //$NON-NLS-1$
                    maxSize, _originalSize));
            return false;
        }
        return ParamsUtil.isSupported(_values, errors);
    }
    
    /**
     * @param builder
     * @return string representing comma separated list of all collection values source code 
     */
    protected String getValuesSourceCode(TestSuiteBuilder builder)
    {
        StringBuilder sb = new StringBuilder();
        for (IParameter param : _values) {
            if (sb.length() > 0) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(param.toSouceCode(builder));
        }
        return sb.toString();
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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        CollectionParam other = (CollectionParam)obj;
        if (_originalSize != other._originalSize) {
            return false;
        }
        return ParamsUtil.equals(_values, other._values);
    }
    
     
}
