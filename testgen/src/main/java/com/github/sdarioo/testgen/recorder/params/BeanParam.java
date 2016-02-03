/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.text.MessageFormat;
import java.util.*;

import org.objectweb.asm.commons.Method;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.instrument.InstrumentUtil;
import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.recorder.params.beans.Bean;
import com.github.sdarioo.testgen.recorder.params.beans.Field;

public class BeanParam
    extends AbstractParam
{
    private final Bean _bean;
    
    private final Field[] _fields;
    private final IParameter[] _params;
    
    public BeanParam(Object obj, Bean bean)
    {
        this(obj, bean, null);
    }
    
    public BeanParam(Object obj, Bean bean, java.lang.reflect.Type genericType)
    {
        super(obj.getClass(), genericType);
        _bean = bean;
    
        _fields = new Field[bean.getFields().size()];
        _params = new IParameter[_fields.length];
        for (int i = 0; i < _params.length; i++) {
            _fields[i] = bean.getFields().get(i);
            _params[i] = getFieldValue(obj, _fields[i]);
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
         
        String type = builder.getTypeName(getRecordedType());
        
        StringBuilder sb = new StringBuilder();
        for (IParameter param : _params) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(param.toSouceCode(builder));
        }
        
        String factoryMethodName = getFactoryMethodName(type);
        String factoryMethodTemplate = getFactoryMethodTemplate(builder);
        TestMethod factoryMethod = builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        
        return MessageFormat.format("{0}({1})", factoryMethod.getName(), sb.toString());
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
        BeanParam other = (BeanParam)obj;
        return getRecordedType().equals(other.getRecordedType()) &&
                ParamsUtil.equals(_params, other._params);
    }
    
    @Override
    public int hashCode() 
    {
        return getRecordedType().hashCode() + 31 * ParamsUtil.hashCode(_params);
    }
    
    private static IParameter getFieldValue(Object obj, Field field)
    {
        try {
            java.lang.reflect.Field refield = obj.getClass().getDeclaredField(field.getName());
            boolean accessible = refield.isAccessible();
            refield.setAccessible(true);
            Object value = refield.get(obj);
            java.lang.reflect.Type genericType = refield.getGenericType();
            
            
            
            
            refield.setAccessible(accessible);
            return ParamsFactory.newValue(value, genericType);
        } catch (Throwable e) {
            Logger.error(e.toString());
        }
        return IParameter.NULL;
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
    private String getFactoryMethodTemplate(TestSuiteBuilder builder)
    {
        String template = builder.getTemplatesCache().get(getRecordedType()); // TODO - generic type
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
            String className = InstrumentUtil.isPrimitive(type) ? 
                    type.getClassName() : 
                    type.getInternalName().replace('/', '.');
                    
            className = builder.getTypeName(className);
            args.append(className).append(' ').append(paramName(field));
        }
        
        Set<Field> fieldsToSet = new HashSet<Field>(Arrays.asList(_fields));
        
        // Constructor arg values
        StringBuilder cvals = new StringBuilder();
        for (Field field : _bean.getConstructor().getFields()) {
            if (cvals.length() > 0) {
                cvals.append(", ");
            }
            cvals.append(paramName(field));
            fieldsToSet.remove(field);
        }
        String returnType = builder.getGenericTypeName(getType());
        if (returnType == null) {
            returnType = builder.getTypeName(getRecordedType()); 
        }
        String instanceType = getRecordedType().equals(ParamsUtil.getRawType(getType())) ? 
                returnType : builder.getTypeName(getRecordedType()); 
        
        StringBuilder sb = new StringBuilder();
        
        // Signature
        sb.append(MessageFormat.format("private static {0} '{'0'}'({1}) <<\n", returnType, args.toString()));
        
        // Constructor call
        sb.append(MessageFormat.format("    {0} result = new {1}({2});\n", returnType, instanceType, cvals.toString()));
        
        // Setter calls + direct field set
        for (Field field : fieldsToSet) {
            Method method = _bean.getSetters().get(field);
            if (method != null) {
                sb.append(MessageFormat.format("    result.{0}({1});\n", method.getName(), paramName(field)));
            } else {
                sb.append(MessageFormat.format("    result.{0} = {1};\n", field.getName(), paramName(field)));
            }
        }
        
        sb.append("    return result;\n");
        sb.append(">>");
        
        template = sb.toString().replace("<<", "'{'").replace(">>", "'}'");
        builder.getTemplatesCache().put(getRecordedType(), template);
        return template;
    }
    
    private static String paramName(Field field)
    {
        String name = field.getName();
        if (name.startsWith("_")) { //$NON-NLS-1$
            name = name.substring(1);
        }
        return name;
    }
}
