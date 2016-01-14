/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.commons.Method;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.recorder.params.beans.Bean;
import com.github.sdarioo.testgen.recorder.params.beans.Field;

public class BeanParam
    implements IParameter
{
    private final Bean _bean;
    private final Class<?> _clazz;
    
    private final Field[] _fields;
    private final IParameter[] _params;
    
    private static Map<Class<?>, String> _factoryTemplateMethodsCache =
            new HashMap<Class<?>, String>();
    
    public BeanParam(Object obj, Bean bean)
    {
        _bean = bean;
        _clazz = obj.getClass();
    
        _fields = new Field[bean.getFields().size()];
        _params = new IParameter[_fields.length];
        for (int i = 0; i < _params.length; i++) {
            _fields[i] = bean.getFields().get(i);
            Object fieldValue = getFieldValue(obj, _fields[i]);
            _params[i] = ParamsFactory.newValue(fieldValue);
        }
    }

    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        boolean bResult = true;
        for (IParameter param : _params) {
            if (!param.isSupported(errors)) {
                bResult = false;
            }
        }
        return bResult;
    }

    @SuppressWarnings("nls")
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        String type = builder.getTypeName(_clazz);
        
        StringBuilder sb = new StringBuilder();
        for (IParameter param : _params) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(param.toSouceCode(builder));
        }
        
        String factoryMethodName = getFactoryMethodName(type);
        String factoryMethodTemplate = createFactoryMethodTemplate(builder);
        TestMethod factoryMethod = builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        
        return MessageFormat.format("{0}({1})", factoryMethod.getName(), sb.toString());
    }
    
    private static Object getFieldValue(Object obj, Field field)
    {
        try {
            java.lang.reflect.Field refield = obj.getClass().getDeclaredField(field.getName());
            refield.setAccessible(true);
            return refield.get(obj);
        } catch (Throwable e) {
            Logger.error(e.toString());
        }
        return null;
    }
    
    private static String getFactoryMethodName(String typeName)
    {
        int index = typeName.lastIndexOf('.');
        if (index > 0) {
            typeName = typeName.substring(index + 1);
        }
        return "new" + typeName; //$NON-NLS-1$
    }
    
    @SuppressWarnings("nls")
    private String createFactoryMethodTemplate(TestSuiteBuilder builder)
    {
        String template = _factoryTemplateMethodsCache.get(_clazz);
        if (template != null) {
            return template;
        }
        
        // Method signature arguments
        StringBuilder args = new StringBuilder();
        for (Field field : _fields) {
            if (args.length() > 0) {
                args.append(", ");
            }
            org.objectweb.asm.Type type = org.objectweb.asm.Type.getType(field.getDesc());
            // TODO - convert to simple type
            String className = type.getClassName();
            args.append(className).append(' ').append(field.getName());
        }
        
        // Constructor arg values
        StringBuilder cvals = new StringBuilder();
        for (Field field : _bean.getConstructor().getFields()) {
            if (cvals.length() > 0) {
                cvals.append(", ");
            }
            cvals.append(field.getName());
        }
        
        StringBuilder sb = new StringBuilder();
        String type = builder.getTypeName(_clazz);
        
        // Signature
        sb.append(MessageFormat.format("private static {0} '{'0'}'({1}) <\n", type, args.toString()));
        
        // Constructor call
        sb.append(MessageFormat.format("    {0} result = new {0}({1});\n", type, cvals.toString()));
        
        // Setter calls 
        for (Map.Entry<Field, Method> setter : _bean.getSetters().entrySet()) {
            Field field = setter.getKey();
            Method method = setter.getValue();
            sb.append(MessageFormat.format("    result.{0}({1});\n", method.getName(), field.getName()));
        }
        sb.append("    return result;\n");
        sb.append(">");
        
        template = sb.toString().replace("<", "'{'").replace(">", "'}'");
        _factoryTemplateMethodsCache.put(_clazz, template);
        return template;
    }
}
