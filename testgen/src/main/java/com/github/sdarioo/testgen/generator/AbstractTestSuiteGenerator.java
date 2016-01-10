/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.lang3.ClassUtils;

import com.github.sdarioo.testgen.generator.source.TestClass;
import com.github.sdarioo.testgen.recorder.Call;

public abstract class AbstractTestSuiteGenerator 
    implements ITestSuiteGenerator
{
    private final boolean _fullTypeNames;

    protected AbstractTestSuiteGenerator(boolean fullTypeNames)
    {
        _fullTypeNames = fullTypeNames;
    }
    
    protected abstract void initTestSuite(Class<?> clazz, TestSuiteBuilder builder);
    
    protected abstract void addTestCases(Method method, List<Call> calls, TestSuiteBuilder builder);
    
    
    @Override
    public TestClass generate(Class<?> testedClass, List<Call> recordedCalls)
    {
        Map<Method, List<Call>> callMap = groupByMethod(recordedCalls);
        TestSuiteBuilder builder = new TestSuiteBuilder();
        initTestSuite(testedClass, builder);
        
        for (Map.Entry<Method, List<Call>> entry : callMap.entrySet()) {
            Method method = entry.getKey();
            List<Call> methodCalls = entry.getValue();
            addTestCases(method, methodCalls, builder);
        }
        
        return builder.buildTestClass();
    }
    
    protected String getTestCaseName(Method method, IUniqueNamesProvider namesProvider)
    {
        String name = method.getName();
        String defaultName = "test" + name.substring(0, 1).toUpperCase() + name.substring(1); //$NON-NLS-1$
        return namesProvider.newUniqueMethodName(defaultName);
    }
    
    protected String getTestClassName(Class<?> clazz, IUniqueNamesProvider namesProvider)
    {
        String name = ClassUtils.getShortCanonicalName(clazz);
        String defaultName = name + "Test"; //$NON-NLS-1$
        return namesProvider.newUniqueFileName(defaultName);
    }

    protected String getShortName(Class<?> type, TestSuiteBuilder builder)
    {
        String name = fullTypeName(type);
        if (_fullTypeNames) {
            return name;
        }
        name = ClassUtils.getShortCanonicalName(type);
        
        if (type.isArray()) {
            type = getArrayType(type);
        }
        if (!type.isPrimitive()) {
            builder.addImport(ClassUtils.getPackageCanonicalName(type) + 
                    '.' + ClassUtils.getShortCanonicalName(type));
        }
        return name;
    }
    
    protected static String[] getParameterNames(Method method)
    {
        // TODO: use ASM or in Java8 method.getParameters()
        int size = method.getParameterTypes().length;
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = "arg" + i; //$NON-NLS-1$
        }
        return result;
    }
    
    // Returns full type name for given class. See package private Field.getTypeName
    private static String fullTypeName(Class<?> type) 
    {
        if (type.isArray()) {
            try {
                Class<?> cl = type;
                int dimensions = 0;
                while (cl.isArray()) {
                    dimensions++;
                    cl = cl.getComponentType();
                }
                StringBuffer sb = new StringBuffer();
                sb.append(cl.getName());
                for (int i = 0; i < dimensions; i++) {
                    sb.append("[]"); //$NON-NLS-1$
                }
                return sb.toString();
            } catch (Throwable e) { /*FALLTHRU*/ }
        }
        return type.getName();
    }

    private static Class<?> getArrayType(Class<?> type)
    {
        Class<?> cl = type;
        while (cl.isArray()) {
            cl = cl.getComponentType();
        }
        return cl;
    }
    
    private static Map<Method, List<Call>> groupByMethod(List<Call> clazzCalls)
    {
        Map<Method, List<Call>> result = new HashMap<Method, List<Call>>();
        for (Call call : clazzCalls) {
            Method method = call.getMethod();
            List<Call> methodCalls = result.get(method);
            if (methodCalls == null) {
                methodCalls = new ArrayList<Call>();
                result.put(method, methodCalls);
            }
            methodCalls.add(call);
        }
        return result;
    }
}
