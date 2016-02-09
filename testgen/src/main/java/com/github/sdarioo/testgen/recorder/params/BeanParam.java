/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.*;
import java.util.*;

import org.objectweb.asm.commons.Method;

import com.github.sdarioo.testgen.generator.MethodBuilder;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.recorder.params.beans.Bean;
import com.github.sdarioo.testgen.util.TypeUtils;


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
    
    public BeanParam(Object obj, Bean bean, java.lang.reflect.Type paramType)
    {
        super(obj.getClass(), paramType);
        _bean = bean;
    
        int length = bean.getFields().size();
        
        _fields = new Field[length];
        _params = new IParameter[length];
        
        for (int i = 0; i < length; i++) {
            _fields[i] = getField(obj, bean.getFields().get(i).getName());
            _params[i] = getFieldValue(obj, _fields[i]);
        }
    }
    
    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        if (!_bean.isAccessible()) {
            errors.add(fmt("Bean {0} is not accessible.", getRecordedType().getName())); //$NON-NLS-1$
            return false;
        }
        for (Field field : _fields) {
            if (field == null) {
                errors.add(fmt("Bean {0} is not supported.", getRecordedType().getName())); //$NON-NLS-1$
                return false;
            }
        }
        
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
        StringBuilder sb = new StringBuilder();
        for (IParameter param : _params) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(param.toSouceCode(builder));
        }
        
        String factoryMethodName = getFactoryMethodName(builder);
        String factoryMethodTemplate = getFactoryMethodTemplate(builder);
        TestMethod factoryMethod = builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        
        return fmt("{0}({1})", factoryMethod.getName(), sb.toString());
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
    
    private Field getField(Object obj, String name)
    {
        try {
            return obj.getClass().getDeclaredField(name);
        } catch (NoSuchFieldException | SecurityException e) {
            Logger.error(e.getMessage());
            return null;
        }
    }
    
    private IParameter getFieldValue(Object obj, Field field)
    {
        TypeVariable<?>[] typeParams = obj.getClass().getTypeParameters();
        Type[] actualTypeParams = getActualTypeArguments();
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Object value = field.get(obj);
            Type fieldType = field.getGenericType();
            if (TypeUtils.containsTypeVariables(fieldType)) {
                int index = Arrays.asList(typeParams).indexOf(fieldType);
                if (index >= 0 && (typeParams.length == actualTypeParams.length)) {
                    fieldType = actualTypeParams[index];
                } else {
                    fieldType = field.getType();
                }
            }
            field.setAccessible(accessible);
            return ParamsFactory.newValue(value, fieldType);
        } catch (Throwable e) {
            Logger.error(e.toString());
        }
        return IParameter.NULL;
    }
    
    private String getFactoryMethodName(TestSuiteBuilder builder)
    {
        String objectClass = builder.getTypeName(getRecordedType());
        int index = objectClass.lastIndexOf('.');
        if (index > 0) {
            objectClass = objectClass.substring(index + 1);
        }
        return "new" + objectClass; //$NON-NLS-1$
    }
    
    @SuppressWarnings("nls")
    private String getFactoryMethodTemplate(TestSuiteBuilder builder)
    {
        Class<?> clazz = getRecordedType();
        Type type = TypeUtils.parameterize(clazz);
        String typeName = TypeUtils.getName(type, builder);
        
        String template = builder.getTemplatesCache().get(typeName);
        if (template != null) {
            return template;
        }
        
        MethodBuilder methodBuilder = new MethodBuilder(builder);
        methodBuilder.name("###").
            modifier(Modifier.PRIVATE | Modifier.STATIC).
            typeParams(clazz.getTypeParameters()).
            returnType(type);
        
        for (Field field : _fields) {
            methodBuilder.arg(field.getGenericType(), paramName(field));
        }
        
        Set<Field> fieldsToSet = new HashSet<Field>(Arrays.asList(_fields));
        
        // Constructor 
        StringBuilder constructorArgs = new StringBuilder();
        for (Field field : _bean.getConstructor().getFields()) {
            if (constructorArgs.length() > 0) {
                constructorArgs.append(", ");
            }
            constructorArgs.append(paramName(field));
            fieldsToSet.remove(field);
        }
        methodBuilder.statement(fmt("{0} result = new {0}({1})", typeName, constructorArgs.toString()));
        
        // Setters + direct field set
        for (Field field : fieldsToSet) {
            Method method = _bean.getSetters().get(field);
            if (method != null) {
                methodBuilder.statement(fmt("result.{0}({1})", method.getName(), paramName(field)));
            } else {
                methodBuilder.statement(fmt("result.{0} = {1}", field.getName(), paramName(field)));
            }
        }
        
        methodBuilder.statement("return result");
        template = methodBuilder.build();
        template = template.replace("{", "'{'");
        template = template.replace("}", "'}'");
        template = template.replace("###", "{0}");
        
        builder.getTemplatesCache().put(typeName, template);
        return template;
    }
    
    private static String paramName(Field field)
    {
        String result = field.getName();
        if (result.startsWith("_")) { //$NON-NLS-1$
            result = result.substring(1);
        }
        return result;
    }
    
}
