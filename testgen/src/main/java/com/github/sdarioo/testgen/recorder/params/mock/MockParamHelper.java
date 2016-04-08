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
    
    MockParamHelper(RecordingInvocationHandler handler, TestSuiteBuilder builder)
    {
        _handler = handler;
        _builder = builder;
    }
    
    private boolean isStaticField()
    {
        // return _handler.getRefCount() > 1;
        return false; // TODO
    }
    
    private boolean isFactoryMethodWithArgs()
    {
        return !_handler.isMultipleCallsToSameMethod();
    }
    
    public String toSouceCode() 
    {
        _builder.addImport("org.mockito.Mockito"); //$NON-NLS-1$
        
        String factoryMethodName = getFactoryMethodName();
        MethodTemplate factoryMethodTemplate = getFactoryMethodTemplate();
        TestMethod factoryMethod = _builder.addHelperMethod(factoryMethodTemplate, factoryMethodName);
        
        if (isStaticField()) {
            String mockFieldName = _handler.getAttr(FILED_ATTR);
            if (mockFieldName != null) {
                return mockFieldName;
            }
            mockFieldName = _builder.newUniqueFieldName(getFieldName());
            _handler.setAttr(FILED_ATTR, mockFieldName);
            
            String mockType = TypeUtil.getName(_handler.getType(), _builder);
            String mockFieldDecl = fmt("private static final {0} {1} = {2};",  //$NON-NLS-1$
                    mockType, mockFieldName, getFactoryMethodCall(factoryMethod));
            
            _builder.addField(new FieldSrc(mockFieldName, mockFieldDecl));
            return mockFieldName;
        }
        
        return getFactoryMethodCall(factoryMethod);
    }


    @SuppressWarnings("nls")
    private String getFactoryMethodCall(TestMethod factoryMethod)
    {
        List<String> args = new ArrayList<String>();
        if (isFactoryMethodWithArgs()) {
            for (Call call : _handler.getCalls()) {
                Method method = call.getMethod();
                Type[] argTypes = method.getGenericParameterTypes();
                Type resultType = method.getGenericReturnType();
                args.addAll(toSourceCode(call.args(), argTypes, _builder));
                args.add(call.getResult().toSouceCode(resultType, _builder));    
            }
        }
        return fmt("{0}({1})", factoryMethod.getName(), StringUtil.join(args, ", "));
    }
    
    @SuppressWarnings("nls")
    private MethodTemplate getFactoryMethodTemplate()
    {
        Class<?> mockClass = _handler.getType();
        Type mockType = TypeUtil.parameterize(mockClass);
        
        MethodBuilder mb = new MethodBuilder(_builder);
        mb.modifier(Modifier.PRIVATE | Modifier.STATIC);
        mb.returnType(mockType);
        mb.name(MethodTemplate.NAME_VARIABLE);
        mb.typeParams(mockClass.getTypeParameters());
        
        String returnTypeName = TypeUtil.getName(mockClass, _builder);
        
        Collection<Call> calls = _handler.getCalls();
        List<String> stubs = new ArrayList<String>();
        
        boolean isTryCatchNeeded = false;
        for (Call call : calls) {
            Method method = call.getMethod();
            isTryCatchNeeded |= (method.getExceptionTypes().length > 0);
            Type[] argTypes = method.getGenericParameterTypes();
            Type returnType = method.getGenericReturnType();
            String whenStmt;
            
            if (isFactoryMethodWithArgs()) {
                
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
        String mockType = _builder.getTypeName(_handler.getType());
        int index = mockType.lastIndexOf('.');
        if (index > 0) {
            mockType = mockType.substring(index + 1);
        }
        return "new" + mockType + "Mock";
    }

    private String getFieldName()
    {
        String mockType = _builder.getTypeName(_handler.getType());
        int index = mockType.lastIndexOf('.');
        if (index > 0) {
            mockType = mockType.substring(index + 1);
        }
        return mockType.toUpperCase();
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
