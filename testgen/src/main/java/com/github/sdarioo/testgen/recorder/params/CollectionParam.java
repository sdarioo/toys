/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Collection;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.IParameter;

public abstract class CollectionParam
    extends AbstractParam
{
    private final int _originalSize;
    protected final Collection<IParameter> _elements;
    
    protected CollectionParam(Collection<?> collection, 
            Collection<IParameter> elements, 
            Type paramType)
    {
        super(collection.getClass(), paramType);
    
        _originalSize = collection.size();
        _elements = elements;
        
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            return;
        }
        for (Object obj : collection) {
            _elements.add(ParamsFactory.newValue(obj, getElementType()));
        }
    }
    
    protected abstract Class<?> getGeneratedSourceCodeType();
    
    
    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        if (!ParamsUtil.isTypeCompatible(getType(), getGeneratedSourceCodeType())) {
            errors.add("Unsupported type: " + ParamsUtil.getRawTypeName(getType())); //$NON-NLS-1$
            return false;
        }
        
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            errors.add(MessageFormat.format("Collection size exceeds maximum permitted size. Max={0}, size={1}.", //$NON-NLS-1$
                    maxSize, _originalSize));
            return false;
        }
        return ParamsUtil.isSupported(_elements, errors);
    }
    
    /**
     * @param builder
     * @return string representing comma separated list of all collection elements source code 
     */
    protected String getElementsSourceCode(TestSuiteBuilder builder)
    {
        StringBuilder sb = new StringBuilder();
        for (IParameter param : _elements) {
            if (sb.length() > 0) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(param.toSouceCode(builder));
        }
        return sb.toString();
    }

    /**
     * @return collection element generic type or null if collection is not parameterized type
     */
    protected Type getElementType()
    {
        Type[] argTypes = getActualTypeArguments();
        return argTypes.length == 1 ? argTypes[0] : null;
    }
    
    /**
     * @param builder
     * @return string representing collection element specification e.g <String> 
     * or empty string in no generic element information available
     */
    protected String getElementTypeSpec(TestSuiteBuilder builder)
    {
        Type elementType = getElementType();
        String elementTypeName = builder.getGenericTypeName(elementType);
        return (elementTypeName != null) ? ('<' + elementTypeName + '>') : ""; //$NON-NLS-1$
    }
    
    @Override
    public int hashCode() 
    {
        return _elements.hashCode();
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
        return (_originalSize == other._originalSize) && _elements.equals(other._elements);
    }
    
     
}
