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
    
    public JUnitParamsGenerator()
    {
        super(false);
    }
    
    @Override
    protected void initTestSuite(Class<?> clazz, TestSuiteBuilder builder) 
    {
        builder.addImport("org.junit.Assert"); //$NON-NLS-1$
        builder.addImport("org.junit.Test"); //$NON-NLS-1$
        builder.addImport("org.junit.runner.RunWith"); //$NON-NLS-1$
        builder.addImport("junitparams.Parameters"); //$NON-NLS-1$
        builder.addImport("junitparams.JUnitParamsRunner"); //$NON-NLS-1$
        
        
        String pkg = clazz.getPackage().getName();
        String name = getTestClassName(clazz, builder);
        
        String signature =  MessageFormat.format(CLASS_SIGNATURE_TEMPLATE, name);
        builder.setCanonicalName((pkg.length() > 0) ? pkg + '.' + name : name);
        builder.setSignature(signature);
    }
    
    @Override
    protected void addTestCases(Method method, List<Call> calls, TestSuiteBuilder builder) 
    {
        TestMethod testCase = generateTestCase(method, builder);
        TestMethod paramProvider = generateParamsProvider(method, calls, testCase.getName(), builder);
        
        builder.addTestCase(paramProvider);
        builder.addTestCase(testCase);
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

        String source = MessageFormat.format(TEST_METHOD_TEMPLATE, paramProviderName, testCaseName, args, body);
        return new TestMethod(testCaseName, source);
    }
    
    // Protected for junit tests
    protected TestMethod generateParamsProvider(Method method, List<Call> calls, 
            String testCaseName, TestSuiteBuilder builder)
    {
        String name = getParamsProviderMethodName(testCaseName);
        StringBuilder sb = new StringBuilder();
        Set<String> errors = new HashSet<String>();
        for (Call call : calls) {
            if (!call.args().isValid(errors)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(",\n"); //$NON-NLS-1$
            }
            List<IParameter> values = new ArrayList<IParameter>();
            values.addAll(call.args().getValues());
            if (hasReturn(method)) {
                values.add(call.getResult());
            }
            String code = toSourceCode(values, builder);
            sb.append(code);
        }
        String source = MessageFormat.format(PARAMS_PROVIDER_METHOD_TEMPLATE, name, sb.toString());
        if (!errors.isEmpty()) {
            source = getProblemsComment(errors) + source;
        }
        return new TestMethod(name, source);
    }

    // Protected for junit tests
    protected static String toSourceCode(List<IParameter> values, TestSuiteBuilder builder)
    {
        StringBuilder sb = new StringBuilder();
        for (IParameter value : values) {
            appendComma(sb);
            String code = (value != null) ? 
                    value.toSouceCode(builder) : 
                    IParameter.NULL.toSouceCode(builder);
            sb.append(code);
        }
        return MessageFormat.format(PARAMS_LIST_TEMPLATE, sb.toString());
    }
    
    private static boolean hasReturn(Method method)
    {
        return !Void.TYPE.equals(method.getReturnType());
    }
    
    private String getDecl(Class<?> type, String name, TestSuiteBuilder builder)
    {
        return getShortName(type, builder) + ' ' + name;
    }
    
    private String getStaticCall(Method method, String[] args, TestSuiteBuilder builder)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getShortName(method.getDeclaringClass(), builder));
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
        sb.append("// Problems:\n"); //$NON-NLS-1$
        for (String line : errors) {
            sb.append("// " + line + '\n'); //$NON-NLS-1$
        }
        return sb.toString();
    }
    
    @SuppressWarnings("nls")
    private static final String CLASS_SIGNATURE_TEMPLATE = 
        "@RunWith(JUnitParamsRunner.class)\n" + 
        "public class {0}";

    
    @SuppressWarnings("nls")
    private static final String TEST_METHOD_TEMPLATE = 
        "@Test\n" + 
        "@Parameters(method = \"{0}\")\n" + 
        "public void {1}({2}) throws Exception '{'\n" + 
        "    {3}\n" + 
        "'}'";
    
    @SuppressWarnings("nls")
    private static final String PARAMS_PROVIDER_METHOD_TEMPLATE = 
        "private Object[] {0}() '{'\n" +
        "    return new Object[] '{'\n" +
        "{1}\n" +        
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
