/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.util.Properties;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.IParameter;

public class PropertiesParam 
    implements IParameter
{
    private final Properties _value;
    
    public PropertiesParam(Properties value)
    {
        _value = new Properties();
        for (Object key : value.keySet()) {
            String sKey = (String)key;
            String sValue = value.getProperty(sKey);
            _value.setProperty(sKey, sValue);
        }
    }
    
    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        TestMethod toProperties = builder.addHelperMethod(TO_PROPERTIES_TEMPLATE, "props"); //$NON-NLS-1$
        TestMethod pair = builder.addHelperMethod(PAIR_TEMPLATE, "pair"); //$NON-NLS-1$
     
        StringBuilder sb = new StringBuilder();
        
        sb.append(toProperties.getName()).append('(');
        
        int index = 0;
        for (Object key : _value.keySet()) {
            if (index++ > 0) {
                sb.append(", ");
            }
            String sKey = (String)key;
            String sValue = _value.getProperty(sKey);
            sb.append(pair.getName()).append('(');
            sb.append(new StringParam(sKey).toSouceCode(builder));
            sb.append(", ");
            sb.append(sValue != null ? new StringParam(sValue).toSouceCode(builder) : "null");
            sb.append(')');
        }
        sb.append(')');
        
        return sb.toString();
    }
    
    
    @Override
    public String toString() 
    {
        return _value.toString();
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof PropertiesParam)) {
            return false;
        }
        PropertiesParam other = (PropertiesParam)obj;
        return _value.equals(other._value);
    }
    
    @Override
    public int hashCode() 
    {
        return _value.hashCode();
    }

    @SuppressWarnings("nls")
    private static final String PAIR_TEMPLATE =
        "private static String[] {0}(String key, String value) '{'\n" +
        "    return new String[] '{' key, value'}';\n" +
        "'}'";
    
    @SuppressWarnings("nls")
    private static final String TO_PROPERTIES_TEMPLATE = 
            "private static java.util.Properties {0}(String[]... pairs) '{'\n" +
            "    java.util.Properties p = new java.util.Properties();\n" +
            "    for (String[] pair : pairs) '{'\n" +
            "        p.setProperty(pair[0], pair[1]);\n" +
            "    '}'\n" +
            "    return p;\n" +
            "}";
    
}
