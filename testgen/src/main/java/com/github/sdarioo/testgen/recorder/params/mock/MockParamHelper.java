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
        
        if (handler.getRefCount() > 1) {
            return MockingStrategy.Singleton;
        }
        
        List<Call> calls = handler.getCalls();
        Set<Method> methods = handler.getMethods();
        
        // If more than one call to same method than we must generate no-args factory method
        if (calls.size() > methods.size()) {
            return MockingStrategy.NoArgsFactoryMethod;
        } else {
            return MockingStrategy.FactoryMethodWithArgs;
        }
    }
    
    public static String toSouceCode(RecordingInvocationHandler handler, TestSuiteBuilder builder) 
    {
        MockingStrategy strategy = MockParamHelper.getMockingStrategy(handler);
        String factoryMethodName = getFactoryMethodName(handler, strategy, builder);
        
        MethodTemplate factoryMethodTemplate = getFactoryMethodTemplate(handler, builder);
        TestMethod factoryMethod = builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        
        List<String> args = new ArrayList<String>();
        
        if (strategy == MockingStrategy.FactoryMethodWithArgs) {
            for (Call call : handler.getCalls()) {
                Method method = call.getMethod();
                Type[] argTypes = method.getGenericParameterTypes();
                Type resultType = method.getGenericReturnType();
                args.addAll(toSourceCode(call.args(), argTypes, builder));
                args.add(call.getResult().toSouceCode(resultType, builder));    
            }    
        }
        
        builder.addImport("org.mockito.Mockito"); //$NON-NLS-1$
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
            Type returnType = method.getGenericReturnType();
            String whenStmt;
            
            if (strategy == MockingStrategy.FactoryMethodWithArgs) {
                
                String[] args = ArgNamesCache.getArgNames(method, true);
                String whenArg = StringUtil.join(args, ", ");
                String thenArg = resultArgName(method.getName());
                
                mb.args(argTypes, args);
                mb.arg(returnType, thenArg);
                
                whenStmt = stub(method.getName(), returnType, whenArg,  thenArg);
            } else {
                List<String> args = toSourceCode(call.args(), argTypes, builder);
                String whenArg = StringUtil.join(args, ", ");
                String thenArg = call.getResult().toSouceCode(returnType, builder);
                 
                whenStmt = stub(method.getName(), returnType, whenArg, thenArg);
            }
            mb.statement(whenStmt);
        }
        
        mb.statement(fmt("return mock"));
        String template = mb.build();
        return new MethodTemplate(template);
    }
    
    @SuppressWarnings("nls")
    private static String stub(String methodName, Type returnType, String whenArg, String thenArg)
    {
        if (TypeUtil.containsWildcards(returnType)) {
            return fmt("Mockito.doReturn({0}).when(mock).{1}({2})", thenArg, methodName, whenArg);
        }
        return fmt("Mockito.when(mock.{0}({1})).thenReturn({2})", methodName, whenArg, thenArg);
    }
    
    @SuppressWarnings("nls")
    private static String getFactoryMethodName(RecordingInvocationHandler handler, MockingStrategy strategy, TestSuiteBuilder builder)
    {
        String returnType = builder.getTypeName(handler.getInterface());
        int index = returnType.lastIndexOf('.');
        if (index > 0) {
            returnType = returnType.substring(index + 1);
        }
        if (strategy == MockingStrategy.Singleton) {
            return "get" + returnType + "Mock";
        } else {
            return "new" + returnType + "Mock";
        }
    }

    private static List<String> toSourceCode(List<IParameter> values, Type[] types, TestSuiteBuilder builder)
    {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < types.length; i++) {
            result.add(values.get(i).toSouceCode(types[i], builder));
        }
        return result;
    }
    
    private static String resultArgName(String methodName)
    {
        if (Character.isUpperCase(methodName.charAt(0))) {
            methodName = StringUtils.uncapitalize(methodName);
        }
        return methodName + "Result";
    }
    
    private static String fmt(String pattern, Object... args)
    {
        return MessageFormat.format(pattern, args);
    }
}
