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
import com.github.sdarioo.testgen.generator.source.FieldSrc;
import com.github.sdarioo.testgen.generator.source.MethodTemplate;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.ArgNamesCache;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.util.StringUtil;
import com.github.sdarioo.testgen.util.TypeUtil;

public class MockParamHelper 
{
    private final RecordingInvocationHandler _handler;
    private final TestSuiteBuilder _builder;
    private final MockingStrategy _strategy;
    
    private final String _mockStaticField;
    
    MockParamHelper(RecordingInvocationHandler handler, TestSuiteBuilder builder)
    {
        _handler = handler;
        _builder = builder;
        _strategy = getMockingStrategy(handler);
        
        _mockStaticField = (_strategy == MockingStrategy.StaticField) ? 
                getMockFieldName(_handler, _builder) : null;
    }
    
    private static MockingStrategy getMockingStrategy(RecordingInvocationHandler handler)
    {
        if (!handler.isSupported(new HashSet<String>())) {
            return MockingStrategy.None;
        }
        
        if (handler.getRefCount() > 1) {
            return MockingStrategy.StaticField;
        }
        
        List<Call> calls = handler.getCalls();
        Set<Method> methods = handler.getMethods();
        
        // If more than one call to same method than we must generate no-args factory method
        if (calls.size() > methods.size()) {
            return MockingStrategy.StaticField;
        } else {
            return MockingStrategy.FactoryMethodWithArgs;
        }
    }
    
    public String toSouceCode() 
    {
        _builder.addImport("org.mockito.Mockito"); //$NON-NLS-1$
        
        if (_strategy == MockingStrategy.StaticField) {
            return toSourceCodeUsingStaticField();
        }
        
        if (_strategy == MockingStrategy.FactoryMethodWithArgs) {
            return toSourceCodeUsingFactoryMethodWithArgs();
        }
        return "null"; //$NON-NLS-1$
    }
    
    private String toSourceCodeUsingStaticField()
    {
        String sourceCode = _handler.getAttr(FILED_ATTR);
        if (sourceCode != null) {
            return sourceCode;
        }
        _handler.setAttr(FILED_ATTR, _mockStaticField);
        
        String factoryMethodName = getFactoryMethodName();
        MethodTemplate factoryMethodTemplate = getFactoryMethodTemplate();
        TestMethod factoryMethod = _builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        String mockType = TypeUtil.getName(_handler.getInterface(), _builder);
        
        String fieldDecl = fmt("private static final {0} {1} = {2}();",  //$NON-NLS-1$
                mockType, _mockStaticField, factoryMethod.getName());
        
        _builder.addField(new FieldSrc(_mockStaticField, fieldDecl));
        
        return _mockStaticField;
    }
    
    @SuppressWarnings("nls")
    private String toSourceCodeUsingFactoryMethodWithArgs()
    {
        String factoryMethodName = getFactoryMethodName();
        MethodTemplate factoryMethodTemplate = getFactoryMethodTemplate();
        TestMethod factoryMethod = _builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        
        List<String> args = new ArrayList<String>();
        for (Call call : _handler.getCalls()) {
            Method method = call.getMethod();
            Type[] argTypes = method.getGenericParameterTypes();
            Type resultType = method.getGenericReturnType();
            args.addAll(toSourceCode(call.args(), argTypes, _builder));
            args.add(call.getResult().toSouceCode(resultType, _builder));    
        }
        return fmt("{0}({1})", factoryMethod.getName(), StringUtil.join(args, ", "));
    }

    @SuppressWarnings("nls")
    private MethodTemplate getFactoryMethodTemplate()
    {
        Class<?> mockClass = _handler.getInterface();
        Type mockType = TypeUtil.parameterize(mockClass);
        
        MethodBuilder mb = new MethodBuilder(_builder);
        mb.modifier(Modifier.PRIVATE | Modifier.STATIC);
        mb.returnType(mockType);
        mb.name(MethodTemplate.NAME_VARIABLE);
        mb.typeParams(mockClass.getTypeParameters());
        
        String returnTypeName = TypeUtil.getName(mockClass, _builder);
        
        List<Call> calls = _handler.getCalls();
        List<String> stubs = new ArrayList<String>();
        
        boolean isTryCatchNeeded = false;
        for (Call call : calls) {
            Method method = call.getMethod();
            isTryCatchNeeded |= (method.getExceptionTypes().length > 0);
            Type[] argTypes = method.getGenericParameterTypes();
            Type returnType = method.getGenericReturnType();
            String whenStmt;
            
            if (_strategy == MockingStrategy.FactoryMethodWithArgs) {
                
                String[] args = ArgNamesCache.getArgNames(method, true);
                String whenArg = StringUtil.join(args, ", ");
                String thenArg = toResultArgName(method.getName());
                
                mb.args(argTypes, args);
                mb.arg(returnType, thenArg);
                
                whenStmt = stub(method.getName(), whenArg,  thenArg, returnType);
            } else {
                List<String> args = toSourceCode(call.args(), argTypes, _builder);
                String whenArg = StringUtil.join(args, ", ");
                String thenArg = call.getResult().toSouceCode(returnType, _builder);
                 
                whenStmt = stub(method.getName(), whenArg, thenArg, returnType);
            }
            stubs.add(whenStmt);
        }
        
        mb.statement(fmt("{0} mock = Mockito.mock({0}.class)", returnTypeName));
        if (isTryCatchNeeded) {
            mb.tryCatch(stubs, "Exception e", "Assert.fail(e.toString())");
        } else {
            mb.statements(stubs);
        }
        mb.statement("return mock");
        
        String template = mb.build();
        
        return new MethodTemplate(template);
    }
    
    @SuppressWarnings("nls")
    private static String stub(String methodName, String whenArg, String thenArg, Type returnType)
    {
        if (TypeUtil.containsWildcards(returnType)) {
            return fmt("Mockito.doReturn({0}).when(mock).{1}({2})", thenArg, methodName, whenArg);
        }
        return fmt("Mockito.when(mock.{0}({1})).thenReturn({2})", methodName, whenArg, thenArg);
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
    private String getFactoryMethodName()
    {
        String returnType = _builder.getTypeName(_handler.getInterface());
        int index = returnType.lastIndexOf('.');
        if (index > 0) {
            returnType = returnType.substring(index + 1);
        }
        return "new" + returnType + "Mock";
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
    
    private static String toResultArgName(String methodName)
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
    
    private static final String FILED_ATTR = "field"; //$NON-NLS-1$
}
