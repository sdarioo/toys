/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params.mock;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import com.github.sdarioo.testgen.generator.MethodBuilder;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.MethodTemplate;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.ArgNamesCache;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.util.StringUtil;
import com.github.sdarioo.testgen.util.TypeUtil;

public final class MockParamHelper 
{
    private MockParamHelper() {}
    
    
    public static MockingStrategy getMockingStrategy(RecordingInvocationHandler handler)
    {
        if (!handler.isSupported(new HashSet<String>())) {
            return MockingStrategy.None;
        }
        
        List<Call> calls = handler.getCalls();
        Set<Method> methods = handler.getMethods();
        
        // If more than one call to same method than we must generate no-args factory method
        if (calls.size() > methods.size()) {
            return MockingStrategy.NoArgsFactoryMethod;
        }
        return MockingStrategy.FactoryMethodWithArgs;
    }
    
    public static String toSouceCode(RecordingInvocationHandler handler, 
            String factoryMethodName, TestSuiteBuilder builder) 
    {
        MethodTemplate factoryMethodTemplate = getFactoryMethodTemplate(handler, builder);
        TestMethod factoryMethod = builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        
        List<String> args = new ArrayList<String>();
        MockingStrategy strategy = MockParamHelper.getMockingStrategy(handler);
        
        if (strategy == MockingStrategy.FactoryMethodWithArgs) {
            for (Call call : handler.getCalls()) {
                Method method = call.getMethod();
                Type[] argTypes = method.getGenericParameterTypes();
                Type resultType = method.getGenericReturnType();
                args.addAll(toSourceCode(call.args(), argTypes, builder));
                args.add(call.getResult().toSouceCode(resultType, builder));    
            }    
        }
        
        return fmt("{0}({1})", factoryMethod.getName(), StringUtil.join(args, ", ")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @SuppressWarnings("nls")
    public static MethodTemplate getFactoryMethodTemplate(RecordingInvocationHandler handler, TestSuiteBuilder builder)
    {
        MockingStrategy strategy = getMockingStrategy(handler);
        
        Class<?> mockClass = handler.getInterface();
        Type mockType = TypeUtil.parameterize(mockClass);
        
        MethodBuilder mb = new MethodBuilder(builder);
        mb.modifier(Modifier.PRIVATE | Modifier.STATIC);
        mb.returnType(mockType);
        mb.name(MethodTemplate.NAME_VARIABLE);
        mb.typeParams(mockClass.getTypeParameters());
        
        String returnTypeName = TypeUtil.getName(mockClass, builder);
        mb.statement(fmt("{0} mock = Mockito.mock({0}.class)", returnTypeName));
        
        List<Call> calls = handler.getCalls();
        for (Call call : calls) {
            Method method = call.getMethod();
            Type[] argTypes = method.getGenericParameterTypes();
            Type resultType = method.getGenericReturnType();
            String whenStmt;
            
            if (strategy == MockingStrategy.FactoryMethodWithArgs) {
                
                String[] argNames = ArgNamesCache.getArgNames(method, true);
                String resultArgName = methodNameToArgName(method.getName());
                
                mb.args(argTypes, argNames);
                mb.arg(resultType, resultArgName);
                
                whenStmt = fmt("Mockito.when(mock.{0}({1})).thenReturn({2})", 
                        method.getName(), 
                        StringUtil.join(argNames, ", "), 
                        resultArgName);
            } else {
                List<String> argSourceCode = toSourceCode(call.args(), argTypes, builder);
                whenStmt = fmt("Mockito.when(mock.{0}({1})).thenReturn({2})", 
                        method.getName(), 
                        StringUtil.join(argSourceCode, ", "), 
                        call.getResult().toSouceCode(resultType, builder));
            }
            mb.statement(whenStmt);
        }
        
        mb.statement(fmt("return mock"));
        String template = mb.build();
        return new MethodTemplate(template);
    }

    private static List<String> toSourceCode(List<IParameter> values, Type[] types, TestSuiteBuilder builder)
    {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < types.length; i++) {
            result.add(values.get(i).toSouceCode(types[i], builder));
        }
        return result;
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
    
    private static String fmt(String pattern, Object... args)
    {
        return MessageFormat.format(pattern, args);
    }
}
