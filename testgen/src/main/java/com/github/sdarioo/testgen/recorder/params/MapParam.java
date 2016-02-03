/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.IParameter;

public class MapParam
    extends AbstractParam
{
    private final int _originalSize;
    
    private final Map<IParameter, IParameter> _elements;

    public MapParam(Map<?,?> map)
    {
        this(map, null);
    }
    
    public MapParam(Map<?,?> map, Type genericType)
    {
        super(map.getClass(), genericType);
        
        _originalSize = map.size();
        _elements = new HashMap<IParameter, IParameter>();
        
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            return;
        }
        
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            IParameter key = ParamsFactory.newValue(entry.getKey(), getKeyType());
            IParameter value = ParamsFactory.newValue(entry.getValue(), getValueType());
            _elements.put(key, value);
        }
    }
    
    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        if (!ParamsUtil.isTypeCompatible(getType(), getGeneratedSourceCodeType())) {
            errors.add("Unsupported type: " + ParamsUtil.getRawTypeName(getType())); //$NON-NLS-1$
            return false;
        }
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            errors.add(MessageFormat.format("Map size exceeds maximum permitted size. Max={0}, current={1}.", //$NON-NLS-1$
                    maxSize, _originalSize));
            return false;
        }
        boolean isSupported = true;
        for (Map.Entry<IParameter, IParameter> entry : _elements.entrySet()) {
            isSupported = isSupported & entry.getKey().isSupported(errors) && entry.getValue().isSupported(errors);
        }
        return isSupported;
    }
    
    protected Class<?> getGeneratedSourceCodeType() 
    {
        return Map.class;
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        builder.addImport(Map.class.getName());
        builder.addImport(HashMap.class.getName());
        
        String asMapTemplate = getAsMapTemplate(builder);
        TestMethod asMap = builder.addHelperMethod(asMapTemplate, "asMap"); //$NON-NLS-1$
        TestMethod asPair = builder.addHelperMethod(AS_PAIR_TEMPLATE, "asPair"); //$NON-NLS-1$

        String elements = getElementsSourceCode(asPair, builder);
        
        return MessageFormat.format("{0}({1})", asMap.getName(), elements);
    }

    private Type getKeyType()
    {
        if (getType() instanceof ParameterizedType) {
            return ((ParameterizedType)getType()).getActualTypeArguments()[0];
        }
        return null;
    }
    
    private Type getValueType()
    {
        if (getType() instanceof ParameterizedType) {
            return ((ParameterizedType)getType()).getActualTypeArguments()[1];
        }
        return null;
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
        MapParam other = (MapParam)obj;
        if (_originalSize != other._originalSize) {
            return false;
        }
        return _elements.equals(other._elements);
    }
    
    @Override
    public int hashCode() 
    {
        return _elements.hashCode();
    }
    
    protected String getElementsSourceCode(TestMethod asPair, TestSuiteBuilder builder)
    {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<IParameter, IParameter> entry : _elements.entrySet()) {
            if (sb.length() > 0) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(asPair.getName());
            sb.append('(');
            sb.append(entry.getKey().toSouceCode(builder));
            sb.append(", "); //$NON-NLS-1$
            sb.append(entry.getValue().toSouceCode(builder));
            sb.append(')');
        }
        return sb.toString();
    }
    
    @SuppressWarnings("nls")
    private String getAsMapTemplate(TestSuiteBuilder builder)
    {
        Type keyType = getKeyType();
        Type valType = getValueType();
        
        String keyTypeName = builder.getGenericTypeName(keyType);
        String valTypeName = builder.getGenericTypeName(valType);
        
        String mapType = "";
        String castKey = "";
        String castVal = "";
        if ((keyTypeName != null) && (valTypeName != null)) {
            mapType = MessageFormat.format("<{0}, {1}>", keyTypeName, valTypeName);
            castKey = '(' + keyTypeName + ')';
            castVal = '(' + valTypeName + ')';
        }
        String template = AS_MAP_TEMPLATE_TEMPLATE.replace("#1#", mapType);
        template = template.replace("#2#", castKey);
        template = template.replace("#3#", castVal);
        return template;
    }
    
    
    @SuppressWarnings("nls")
    private static final String AS_PAIR_TEMPLATE =
        "private static Object[] {0}(Object key, Object value) '{'\n" +
        "    return new Object[] '{' key, value'}';\n" +
        "'}'";
    
    @SuppressWarnings("nls")
    private static final String AS_MAP_TEMPLATE_TEMPLATE = 
            "private static Map#1# {0}(Object[]... pairs) '{'\n" +
            "    Map#1# map = new HashMap#1#();\n" +
            "    for (Object[] pair : pairs) '{'\n" +
            "        map.put(#2#pair[0], #3#pair[1]);\n" +
            "    '}'\n" +
            "    return map;\n" +
            "}";
}
