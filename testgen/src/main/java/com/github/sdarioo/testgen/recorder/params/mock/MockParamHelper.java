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
        
        String singletonFieldName = null;
        if (strategy == MockingStrategy.Singleton) {
            String sourceCode = handler.getAttr("sourcecode");
            if (sourceCode != null) {
                return sourceCode;
            }
            singletonFieldName = getMockFieldName(handler, builder);
            handler.setAttr("sourcecode", singletonFieldName);
        }
        
        
        String factoryMethodName = getFactoryMethodName(handler, strategy, builder);
        
        MethodTemplate factoryMethodTemplate = getFactoryMethodTemplate(handler, builder, singletonFieldName);
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
    private static MethodTemplate getFactoryMethodTemplate(RecordingInvocationHandler handler, 
            TestSuiteBuilder builder, String mockFieldName)
    {
        MockingStrategy strategy = getMockingStrategy(handler);
        
        Class<?> mockClass = handler.getInterface();
        Type mockType = TypeUtil.parameterize(mockClass);
        
        MethodBuilder mb = new MethodBuilder(builder);
        mb.modifier(Modifier.PRIVATE | Modifier.STATIC);
        mb.returnType(mockType);
        mb.name(MethodTemplate.NAME_VARIABLE);
        mb.typeParams(mockClass.getTypeParameters());
        mb.exception(Exception.class.getName());
        
        String returnTypeName = TypeUtil.getName(mockClass, builder);
        String mockName = (strategy == MockingStrategy.Singleton) ? mockFieldName : "mock";
        
        List<Call> calls = handler.getCalls();
        List<String> stubs = new ArrayList<String>();
        
        for (Call call : calls) {
            Method method = call.getMethod();
            Type[] argTypes = method.getGenericParameterTypes();
            Type returnType = method.getGenericReturnType();
            String whenStmt;
            
            if (strategy == MockingStrategy.FactoryMethodWithArgs) {
                
                String[] args = ArgNamesCache.getArgNames(method, true);
                String whenArg = StringUtil.join(args, ", ");
                String thenArg = toArgName(method.getName());
                
                mb.args(argTypes, args);
                mb.arg(returnType, thenArg);
                
                whenStmt = stub(mockName, method.getName(), whenArg,  thenArg, returnType);
            } else {
                List<String> args = toSourceCode(call.args(), argTypes, builder);
                String whenArg = StringUtil.join(args, ", ");
                String thenArg = call.getResult().toSouceCode(returnType, builder);
                 
                whenStmt = stub(mockName, method.getName(), whenArg, thenArg, returnType);
            }
            stubs.add(whenStmt);
        }
        
        if (strategy == MockingStrategy.Singleton) {
            // Hack: declare mock static field as annotation in method builder..
            mb.annotation(fmt("private static {0} {1} = null;", returnTypeName, mockName));
            
            List<String> ifBody = new ArrayList<String>();
            ifBody.add(fmt("{0} = Mockito.mock({1}.class)", mockName, returnTypeName));
            ifBody.addAll(stubs);
            mb.ifThen(fmt("{0} == null", mockName), ifBody);
        } else {
            mb.statement(fmt("{0} {1} = Mockito.mock({0}.class)", returnTypeName, mockName));
            mb.statements(stubs);
        }
        
        mb.statement(fmt("return {0}", mockName));
        String template = mb.build();
        
        
        return new MethodTemplate(template);
    }
    
    @SuppressWarnings("nls")
    private static String stub(String mockName, String methodName, String whenArg, String thenArg, Type returnType)
    {
        if (TypeUtil.containsWildcards(returnType)) {
            return fmt("Mockito.doReturn({0}).when({1}).{2}({3})", thenArg, mockName, methodName, whenArg);
        }
        return fmt("Mockito.when({0}.{1}({2})).thenReturn({3})", mockName, methodName, whenArg, thenArg);
    }

    private static List<String> toSourceCode(List<IParameter> values, Type[] types, TestSuiteBuilder builder)
    {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < types.length; i++) {
            result.add(values.get(i).toSouceCode(types[i], builder));
        }
        return result;
    }
    
    @SuppressWarnings("nls")
    private static String getFactoryMethodName(RecordingInvocationHandler handler, 
            MockingStrategy strategy, TestSuiteBuilder builder)
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

    private static String getMockFieldName(RecordingInvocationHandler handler, TestSuiteBuilder builder)
    {
        String name = builder.getTypeName(handler.getInterface());
        int index = name.lastIndexOf('.');
        if (index > 0) {
            name = name.substring(index + 1);
        }
        name = name.toUpperCase();
        return builder.newUniqueFieldName(name);
    }
    
    private static String toArgName(String methodName)
    {
        if (Character.isUpperCase(methodName.charAt(0))) {
            methodName = StringUtils.uncapitalize(methodName);
        }
        return methodName + "Result"; //$NON-NLS-1$
    }
    
    private static String fmt(String pattern, Object... args)
    {
        return MessageFormat.format(pattern, args);
    }
}
