/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.impl;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

import com.github.sdarioo.testgen.generator.AbstractTestSuiteGenerator;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.IParameter;

public class JUnitParamsGenerator
    extends AbstractTestSuiteGenerator
{
    private int _testMethodOrder = 0;
    private int _paramMethodOrder = 1000;
    
    @Override
    protected void initTestSuite(Class<?> clazz, TestSuiteBuilder builder) 
    {
        builder.addImport("org.junit.Assert"); //$NON-NLS-1$
        builder.addImport("org.junit.Test"); //$NON-NLS-1$
        builder.addImport("org.junit.runner.RunWith"); //$NON-NLS-1$
        builder.addImport("junitparams.Parameters"); //$NON-NLS-1$
        builder.addImport("junitparams.JUnitParamsRunner"); //$NON-NLS-1$
        
        
        String pkg = (clazz.getPackage() != null) ? clazz.getPackage().getName() : ""; //$NON-NLS-1$
        String name = getTestClassName(clazz, builder);
        
        String signature =  MessageFormat.format(CLASS_SIGNATURE_TEMPLATE, name);
        builder.setCanonicalName((pkg.length() > 0) ? pkg + '.' + name : name);
        builder.setSignature(signature);
    }
    
    @Override
    protected void addTestCases(Method method, List<Call> callsWithResult, 
            TestSuiteBuilder builder) 
    {
        TestMethod testCase = generateTestCase(method, builder);
        TestMethod paramProvider = generateParamsProvider(method, callsWithResult, testCase.getName(), builder);
        
        builder.addTestCase(paramProvider);
        builder.addTestCase(testCase);
    }
    
    @Override
    protected void addTestCasesForExceptions(Method method, List<Call> callsWithException,
            TestSuiteBuilder builder) 
    {
        for (Call call : callsWithException) {
            if (!call.isSupported(new HashSet<String>())) {
                continue;
            }
            List<String> args = new ArrayList<String>();
            for (IParameter param : call.args()) {
                args.add(param.toSouceCode(builder));
            }
            String testCaseName = getTestCaseName(method, builder);
            String methodInvocation = getStaticCall(method, args.toArray(new String[0]), builder);
            String exceptionName = builder.getTypeName(call.getExceptionInfo().getClassName());
            
            String source = MessageFormat.format(TEST_CASE_WITH_EXCEPTION_TEMPLATE, 
                    exceptionName, testCaseName, methodInvocation);
            
            TestMethod testCase = new TestMethod(testCaseName, source, _testMethodOrder++);
            builder.addTestCase(testCase);
        }
    }
    
    protected TestMethod generateTestCase(Method method, TestSuiteBuilder builder)
    {
        Class<?>[] paramTypes = method.getParameterTypes();
        String[] paramNames = getParameterNames(method);
        assert(paramTypes.length == paramNames.length);

        // Test method signature
        StringBuilder args = new StringBuilder();
        for (int i = 0; i < paramTypes.length; i++) {
            appendComma(args);
            args.append(getDecl(paramTypes[i], paramNames[i], builder));
        }
        if (hasReturn(method)) {
            appendComma(args);
            args.append(getDecl(method.getReturnType(), EXPECTED, builder));
        }
        
        // Tested method invocation and assert
        StringBuilder body = new StringBuilder();
        if (hasReturn(method)) {
            body.append(getDecl(method.getReturnType(), RESULT, builder));
            body.append('=');
        }
        body.append(getStaticCall(method, paramNames, builder));
        body.append(';');
        if (hasReturn(method)) {
            body.append('\n');
            body.append(MessageFormat.format(ASSERT_EQUALS_TEMPLATE, EXPECTED, RESULT));
        }
        
        String testCaseName = getTestCaseName(method, builder);
        String paramProviderName = getParamsProviderMethodName(testCaseName);

        String source = MessageFormat.format(TEST_CASE_TEMPLATE, paramProviderName, testCaseName, args, body);
        return new TestMethod(testCaseName, source, _testMethodOrder++);
    }
    
    // Protected for junit tests
    protected TestMethod generateParamsProvider(Method method, List<Call> calls, 
            String testCaseName, TestSuiteBuilder builder)
    {
        String name = getParamsProviderMethodName(testCaseName);
        Set<String> errors = new HashSet<String>();
        
        StringBuilder sb = new StringBuilder();
        for (Call call : calls) {
            if (!call.isSupported(errors)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(",\n"); //$NON-NLS-1$
            }
            List<IParameter> params = new ArrayList<IParameter>();
            params.addAll(call.args());

            if (hasReturn(method)) {
                params.add(call.getResult());
            }
            String code = toSourceCode(params, builder);
            sb.append(code);
        }
        String source = MessageFormat.format(PARAMS_PROVIDER_METHOD_TEMPLATE, 
                getProblemsComment(errors), name, sb.toString());
        
        return new TestMethod(name, source, _paramMethodOrder++);
    }

    // Protected for junit tests
    protected static String toSourceCode(List<IParameter> params, TestSuiteBuilder builder)
    {
        StringBuilder sb = new StringBuilder();
        for (IParameter param : params) {
            appendComma(sb);
            if (param != null) {
                sb.append(param.toSouceCode(builder));
            } else {
                sb.append("null"); //$NON-NLS-1$
            }
        }
        return MessageFormat.format(PARAMS_LIST_TEMPLATE, sb.toString());
    }
    
    private static boolean hasReturn(Method method)
    {
        return !Void.TYPE.equals(method.getReturnType());
    }
    
    private String getDecl(Class<?> type, String name, TestSuiteBuilder builder)
    {
        return builder.getTypeName(type) + ' ' + name;
    }
    
    private String getStaticCall(Method method, String[] args, TestSuiteBuilder builder)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(builder.getTypeName(method.getDeclaringClass()));
        sb.append('.');
        sb.append(method.getName());
        sb.append('(');
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(args[i]);
        }
        sb.append(')');
        return sb.toString();
    }

    private static String getParamsProviderMethodName(String testCaseName)
    {
        // TestCase name is unique and it guarantees provider name uniqueness
        return testCaseName + "_Parameters"; //$NON-NLS-1$
    }
    
    private static void appendComma(StringBuilder sb)
    {
        if (sb.length() > 0) {
            sb.append(", "); //$NON-NLS-1$
        }
    }
    
    private static String getProblemsComment(Set<String> errors)
    {
        StringBuilder sb = new StringBuilder();
        if (!errors.isEmpty()) {
            int idx = 1;
            sb.append("// Problems while recording parameters:"); //$NON-NLS-1$
            for (String line : errors) {
                sb.append(MessageFormat.format("\n// {0}. {1}", idx++, line)); //$NON-NLS-1$
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
        "    {3}\n" + 
        "'}'";
    
    @SuppressWarnings("nls")
    private static final String TEST_CASE_WITH_EXCEPTION_TEMPLATE = 
        "@Test(expected={0}.class)\n" + 
        "public void {1}() throws Exception '{'\n" + 
        "    {2};\n" + 
        "    Assert.assertTrue(false);\n" +
        "'}'";
    
    @SuppressWarnings("nls")
    private static final String PARAMS_PROVIDER_METHOD_TEMPLATE = 
        "{0}\n"+
        "private Object[] {1}() '{'\n" +
        "    return new Object[] '{'\n" +
        "{2}\n" +        
        "    '}';\n" +
        "'}'";
    
    @SuppressWarnings("nls")
    private static final String ASSERT_EQUALS_TEMPLATE = 
        "    Assert.assertEquals({0}, {1});";
    
    @SuppressWarnings("nls")
    private static final String PARAMS_LIST_TEMPLATE = 
        "        new Object[]'{' {0} '}'";
    
    
    private static final String RESULT = "result"; //$NON-NLS-1$
    private static final String EXPECTED = "expected"; //$NON-NLS-1$
    
}
