/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.sdarioo.testgen.generator.MethodBuilder;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.MethodTemplate;
import com.github.sdarioo.testgen.recorder.ArgNamesCache;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.util.StringUtil;
import com.github.sdarioo.testgen.util.TypeUtil;

public final class ProxyParamSourceCode 
{
    private ProxyParamSourceCode() {}
    
    
    public static boolean isFactoryMethodWithArgs(RecordingInvocationHandler handler)
    {
        List<Call> calls = handler.getCalls();
        Set<Method> methods = handler.getMethods();
        // More than one call to same method
        if (calls.size() > methods.size()) {
            return false;
        }
        return true;
    }
    
    @SuppressWarnings("nls")
    public static MethodTemplate getFactoryMethodTemplate(RecordingInvocationHandler handler, TestSuiteBuilder builder)
    {
        boolean isArgs = isFactoryMethodWithArgs(handler);
        
        List<Call> calls = handler.getCalls();
        Class<?> proxyType = handler.getInterface();
        
        List<Type> argTypes = new ArrayList<Type>();
        List<String> argNames = new ArrayList<String>();
        getArgTypesAndNames(calls, argTypes, argNames);
        
        MethodBuilder methodBuilder = new MethodBuilder(builder);
        methodBuilder.modifier(Modifier.PRIVATE | Modifier.STATIC).
            returnType(proxyType).
            name(MethodTemplate.NAME_VARIABLE);
            
        if (isArgs) {
            methodBuilder.args(argTypes.toArray(new Type[0]), argNames.toArray(new String[0]));
        }
        methodBuilder.statement(fmt("{0} mock = Mockito.mock({0}.class)", TypeUtil.getName(proxyType, builder)));
        
        int argIndex = 0;
        for (Call call : calls) {
            Method method = call.getMethod();
            int paramCount = method.getParameterTypes().length;
            String stmt;
            if (isArgs) {
                String args = StringUtil.join(subList(argNames, argIndex, paramCount), ", ");
                stmt = fmt("Mockito.when(mock.{0}({1})).thenReturn({2})", 
                        method.getName(), 
                        args, 
                        argNames.get(argIndex + paramCount));
            } else {
                String args = toString(method.getParameterTypes(), call.args(), builder);
                stmt = fmt("Mockito.when(mock.{0}({1})).thenReturn({2})", 
                        method.getName(), 
                        args, 
                        call.getResult().toSouceCode(getReturnType(method), builder));
            }
            methodBuilder.statement(stmt);
            argIndex += (paramCount + 1);
        }
        methodBuilder.statement(fmt("return mock"));
        String template = methodBuilder.build();
        return new MethodTemplate(template);
    }
    
    static Type getReturnType(Method method)
    {
        Type type = method.getGenericReturnType();
        return TypeUtil.containsTypeVariables(type) ? method.getReturnType() : type;
    }
    
    private static void getArgTypesAndNames(List<Call> calls, List<Type> typesHolder, List<String> namesHolder)
    {
        for (Call call : calls) {
            Method method = call.getMethod();
            Type[] types = method.getParameterTypes();
            String[] names = ArgNamesCache.getArgNames(method, true);
            
            typesHolder.addAll(Arrays.asList(types));
            namesHolder.addAll(Arrays.asList(names));
            
            Type returnType = method.getGenericReturnType();
            String returnArgName = methodNameToArgName(method.getName());
            
            typesHolder.add(returnType);
            namesHolder.add(returnArgName);
        }
    }
    
    private static String toString(Type[] types, List<IParameter> values, TestSuiteBuilder builder)
    {
        List<String> args = new ArrayList<String>();
        for (int i = 0; i < types.length; i++) {
            args.add(values.get(i).toSouceCode(types[i], builder));
        }
        return StringUtil.join(args, ", "); //$NON-NLS-1$
    }
    
    private static String methodNameToArgName(String methodName)
    {
        if (methodName.length() <= 3) {
            return methodName;
        }
        
        if (methodName.startsWith("get")) { //$NON-NLS-1$
            methodName = methodName.substring(3);
        }
        if (Character.isUpperCase(methodName.charAt(0))) {
            methodName = StringUtils.uncapitalize(methodName);
        }
        return methodName;
    }
    
    private static <T> List<T> subList(List<T> list, int index, int length)
    {
        List<T> result = new ArrayList<T>();
        for (int i = index; i < (index + length); i++) {
            result.add(list.get(i));
        }
        return result;
    }
    
    private static String fmt(String pattern, Object... args)
    {
        return MessageFormat.format(pattern, args);
    }
}
