/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.impl;

import java.lang.reflect.*;
import java.util.*;

import com.github.sdarioo.testgen.generator.AbstractTestSuiteGenerator;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.IParameter;
import com.github.sdarioo.testgen.util.GeneratorUtil;

public class JUnitParamsGenerator
    extends AbstractTestSuiteGenerator
{
    private int _testMethodOrder = 0;
    private int _paramMethodOrder = 1000;
    
    
    @Override
    protected void initTestSuite(Class<?> targetClass, TestSuiteBuilder builder) 
    {
        builder.addImport("org.junit.Assert"); //$NON-NLS-1$
        builder.addImport("org.junit.Test"); //$NON-NLS-1$
        builder.addImport("org.junit.runner.RunWith"); //$NON-NLS-1$
        builder.addImport("junitparams.Parameters"); //$NON-NLS-1$
        builder.addImport("junitparams.JUnitParamsRunner"); //$NON-NLS-1$
        
        
        String pkg = (targetClass.getPackage() != null) ? targetClass.getPackage().getName() : ""; //$NON-NLS-1$
        String name = getTestClassName(targetClass, builder);
        
        String signature =  fmt(CLASS_SIGNATURE_TEMPLATE, name);
        builder.setCanonicalName((pkg.length() > 0) ? pkg + '.' + name : name);
        builder.setSignature(signature);
    }
    
    @Override
    protected void addTestCases(Class<?> targetClass, Method method, 
            List<Call> callsWithResult, 
            TestSuiteBuilder builder) 
    {
        TestMethod testCase = generateTestCase(targetClass, method, builder);
        TestMethod paramProvider = generateParamsProvider(method, callsWithResult, testCase.getName(), builder);
        
        builder.addTestCase(paramProvider);
        builder.addTestCase(testCase);
    }
    
    @Override
    protected void addTestCasesForExceptions(Class<?> targetClass, Method method, 
            List<Call> callsWithException,
            TestSuiteBuilder builder) 
    {
        for (Call call : callsWithException) {
            if (!call.isSupported(new HashSet<String>())) {
                continue;
            }
            List<String> callArgs = new ArrayList<String>();
            for (IParameter param : call.args()) {
                callArgs.add(param.toSouceCode(builder));
            }
            // Tested method invocation - should throw exception
            List<String> body = getCall(targetClass, method, toArray(callArgs), null, builder);
            
            String testCaseName = getTestCaseName(method, builder);
            String exceptionName = builder.getTypeName(call.getExceptionInfo().getClassName());
            
            String source = fmt(TEST_CASE_WITH_EXCEPTION_TEMPLATE, exceptionName, testCaseName, toCodeLines(body, 1));
            
            TestMethod testCase = new TestMethod(testCaseName, source, _testMethodOrder++);
            builder.addTestCase(testCase);
        }
    }
    
    protected TestMethod generateTestCase(Class<?> targetClass, Method method, TestSuiteBuilder builder)
    {
        Class<?>[] paramTypes = method.getParameterTypes();
        Type[] genericTypes = method.getGenericParameterTypes();
        String[] paramNames = getParameterNames(method);
        
        // Tested method invocation and assert
        List<String> body = getCall(targetClass, method, paramNames, RESULT, builder);
        if (hasReturn(method)) {
            body.add(fmt("Assert.assertEquals({0}, {1})", EXPECTED, RESULT)); //$NON-NLS-1$
        }
        
        // Test case method parameter declarations
        List<String> paramsList = new ArrayList<String>();
        for (int i = 0; i < paramTypes.length; i++) {
            paramsList.add(getDecl(paramTypes[i], genericTypes[i], paramNames[i], builder));
        }
        if (hasReturn(method)) {
            paramsList.add(getDecl(method.getReturnType(), method.getGenericReturnType(), EXPECTED, builder));
        }
        
        String params = join(toArray(paramsList));
        String testCaseName = getTestCaseName(method, builder);
        String paramProviderName = getParamsProviderMethodName(testCaseName);

        String source = fmt(TEST_CASE_TEMPLATE, paramProviderName, testCaseName, params, toCodeLines(body, 1));
        return new TestMethod(testCaseName, source, _testMethodOrder++);
    }
    
    // Protected for junit tests
    protected TestMethod generateParamsProvider(Method method, List<Call> calls, 
            String testCaseName, TestSuiteBuilder builder)
    {
        String name = getParamsProviderMethodName(testCaseName);

        Set<String> errors = new HashSet<String>();
        List<String> lines = new ArrayList<String>();
        
        for (Call call : calls) {
            if (!call.isSupported(errors)) {
                continue;
            }
            List<String> callArgs = new ArrayList<String>();
            for (IParameter param : call.args()) {
                callArgs.add(param.toSouceCode(builder));
            }
            if (hasReturn(method)) {
                callArgs.add(call.getResult() != null ? call.getResult().toSouceCode(builder) : "null"); //$NON-NLS-1$
            }
            lines.add(fmt("new Object[]'{' {0} '}'", join(toArray(callArgs)))); //$NON-NLS-1$
        }
        String javadoc = getProblemsComment(errors);
        String source = fmt(PARAMS_PROVIDER_METHOD_TEMPLATE, name, toCodeLines(lines, 2, ','));
        if (javadoc.length() > 0) {
            source = javadoc + '\n' + source;
        }
        return new TestMethod(name, source, _paramMethodOrder++);
    }
    
    private static boolean hasReturn(Method method)
    {
        return !Void.TYPE.equals(method.getReturnType());
    }
    
    private String getDecl(Class<?> type, Type genericType, String name, TestSuiteBuilder builder)
    {
        String typeName = builder.getGenericTypeName(genericType);
        if (typeName == null) {
            typeName = builder.getTypeName(type);
        }
        return typeName + ' ' + name;
    }
    
    private List<String> getCall(Class<?> targetClass, Method method, String[] args, 
            String var, TestSuiteBuilder builder)
    {
        List<String> lines = new ArrayList<String>();
        
        String typeName = builder.getTypeName(targetClass);
        String callTarget;
        
        if (Modifier.isStatic(method.getModifiers())) {
            callTarget = typeName;
        } else {
            Constructor<?> constructor = GeneratorUtil.findConstructor(targetClass);
            String[] constructorArgs = new String[0];
            if (constructor == null) {
                lines.add(fmt("// ERROR - class {0} has no accessible constructors", typeName)); //$NON-NLS-1$
            } else if (constructor.getParameterTypes().length > 0) {
                lines.add(fmt("// WARNING - constructing {0} with default parameters", typeName)); //$NON-NLS-1$
                constructorArgs = GeneratorUtil.getDefaultArgSourceCode(constructor, builder);
            }
            lines.add(fmt("{0} obj = new {0}({1})", typeName, join(constructorArgs))); //$NON-NLS-1$
            callTarget = "obj"; //$NON-NLS-1$
        }
        
        if (hasReturn(method) && (var != null)) {
            String retType = getDecl(method.getReturnType(), method.getGenericReturnType(), var, builder);
            lines.add(fmt("{0} = {1}.{2}({3})", retType, callTarget, method.getName(), join(args))); //$NON-NLS-1$
        } else {
            lines.add(fmt("{0}.{1}({2})", callTarget, method.getName(), join(args))); //$NON-NLS-1$
        }
        return lines;
    }
    
    private static String getParamsProviderMethodName(String testCaseName)
    {
        // TestCase name is unique and it guarantees provider name uniqueness
        return testCaseName + "_Parameters"; //$NON-NLS-1$
    }
    
    private static String join(String[] args)
    {
        StringBuilder sb = new StringBuilder();
        for (String arg : args) {
            if (sb.length() > 0) {
                sb.append(", "); //$NON-NLS-1$
            }
            sb.append(arg);
        }
        return sb.toString();
    }
    
    private static String toCodeLines(List<String> lines, int indentLvl)
    {
        return toCodeLines(lines, indentLvl, ';');
    }
    
    private static String toCodeLines(List<String> lines, int indentLvl, char lineSep)
    {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (sb.length() > 0) {
                sb.append('\n');
            }
            for (int i = 0; i < indentLvl; i++) {
                sb.append("    "); //$NON-NLS-1$
            }
            sb.append(line).append(lineSep);
        }
        return sb.toString();
    }
    
    private static String[] toArray(List<String> list)
    {
        return list.toArray(new String[list.size()]);
    }
    
    private static String getProblemsComment(Set<String> errors)
    {
        StringBuilder sb = new StringBuilder();
        if (!errors.isEmpty()) {
            int idx = 1;
            sb.append("// WARNING - cannot record parameters:"); //$NON-NLS-1$
            for (String line : errors) {
                sb.append(fmt("\n// {0}. {1}", idx++, line)); //$NON-NLS-1$
            }
        }
        return sb.toString();
    }
    
    @SuppressWarnings("nls")
    private static final String CLASS_SIGNATURE_TEMPLATE = 
        "@RunWith(JUnitParamsRunner.class)\n" + 
        "public class {0}";

    
    @SuppressWarnings("nls")
    private static final String TEST_CASE_TEMPLATE = 
        "@Test\n" + 
        "@Parameters(method = \"{0}\")\n" + 
        "public void {1}({2}) throws Exception '{'\n" + 
        "{3}\n" + 
        "'}'";
    
    @SuppressWarnings("nls")
    private static final String TEST_CASE_WITH_EXCEPTION_TEMPLATE = 
        "@Test(expected={0}.class)\n" + 
        "public void {1}() throws Exception '{'\n" + 
        "{2}\n" + 
        "'}'";
    
    @SuppressWarnings("nls")
    private static final String PARAMS_PROVIDER_METHOD_TEMPLATE = 
        "private Object[] {0}() throws Exception '{'\n" +
        "    return new Object[] '{'\n" +
        "{1}\n" +        
        "    '}';\n" +
        "'}'";
    
    private static final String RESULT = "result"; //$NON-NLS-1$
    private static final String EXPECTED = "expected"; //$NON-NLS-1$
    
}
