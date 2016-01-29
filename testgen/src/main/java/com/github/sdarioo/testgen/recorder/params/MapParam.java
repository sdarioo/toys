/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.IParameter;

public class MapParam
    implements IParameter
{
    private final Class<?> _clazz;
    private final int _originalSize;
    
    protected final IParameter[][] _pairs;
    
    private static final Class<?>[] SUPPORTED = {
        java.util.HashMap.class,
        java.util.TreeMap.class
    };
    
    public MapParam(Map<?,?> map)
    {
        this(map, null);
    }
    
    public MapParam(Map<?,?> map, Type mapGenericType)
    {
        _clazz = map.getClass();
        _originalSize = map.size();
        
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if ((_originalSize > maxSize) || !isMapTypeSupported()) {
            _pairs = new IParameter[0][];
            return;
        }
        _pairs = new IParameter[map.size()][];
        
        int i = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            _pairs[i] = new IParameter[2];
            _pairs[i][0] = ParamsFactory.newValue(entry.getKey());
            _pairs[i][1] = ParamsFactory.newValue(entry.getValue());
            i++;
        }
    }
    
    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        if (!isMapTypeSupported()) {
            errors.add("Unsupported map instance: " + _clazz.getName()); //$NON-NLS-1$
            return false;
        }
        int maxSize = Configuration.getDefault().getMaxCollectionSize();
        if (_originalSize > maxSize) {
            errors.add(MessageFormat.format("Map size exceeds maximum permitted size. Max={0}, size={1}.", //$NON-NLS-1$
                    maxSize, _originalSize));
            return false;
        }
        boolean isSupported = true;
        for (IParameter[] pair : _pairs) {
            isSupported = isSupported & pair[0].isSupported(errors) && pair[1].isSupported(errors);
        }
        return isSupported;
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        String template = getFactoryMethodTemplate(builder);
        TestMethod asMap = builder.addHelperMethod(template, "asMap"); //$NON-NLS-1$
        TestMethod asPair = builder.addHelperMethod(AS_PAIR_TEMPLATE, "asPair"); //$NON-NLS-1$
     
        StringBuilder sb = new StringBuilder();
        for (IParameter[] keyValue : _pairs) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(asPair.getName());
            sb.append('(');
            sb.append(keyValue[0].toSouceCode(builder));
            sb.append(", ");
            sb.append(keyValue[1].toSouceCode(builder));
            sb.append(')');
        }
        return MessageFormat.format("{0}({1})", asMap.getName(), sb.toString());
    }
    
    private boolean isMapTypeSupported()
    {
        for (Class<?> supportedMap : SUPPORTED) {
            if (_clazz.equals(supportedMap)) {
                return true;
            }
        }
        return false;
    }

    private String getFactoryMethodTemplate(TestSuiteBuilder builder)
    {
        String template = builder.getTemplatesCache().get(_clazz);
        if (template == null) {
            String mapClass = builder.getTypeName(_clazz);
            template = AS_MAP_TEMPLATE_TEMPLATE.replace("<MAP>", mapClass); //$NON-NLS-1$
            builder.getTemplatesCache().put(_clazz, template);
        }
        return template;
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
        if (_pairs.length != other._pairs.length) {
            return false;
        }
        for (int i = 0; i < _pairs.length; i++) {
            if (!ParamsUtil.equals(_pairs[i], other._pairs[i])) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() 
    {
        int hash = 0;
        for (IParameter[] pair : _pairs) {
            hash = (31 * hash) + ParamsUtil.hashCode(pair);
        }
        return hash;
    }

    @SuppressWarnings("nls")
    private static final String AS_PAIR_TEMPLATE =
        "private static Object[] {0}(Object key, Object value) '{'\n" +
        "    return new Object[] '{' key, value'}';\n" +
        "'}'";
    
    @SuppressWarnings("nls")
    private static final String AS_MAP_TEMPLATE_TEMPLATE = 
            "private static <MAP> {0}(Object[]... pairs) '{'\n" +
            "    <MAP> p = new <MAP>();\n" +
            "    for (Object[] pair : pairs) '{'\n" +
            "        p.put(pair[0], pair[1]);\n" +
            "    '}'\n" +
            "    return p;\n" +
            "}";
}
