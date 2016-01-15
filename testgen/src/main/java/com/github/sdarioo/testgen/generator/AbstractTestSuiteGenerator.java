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
import com.github.sdarioo.testgen.recorder.IArgNamesProvider;

public abstract class AbstractTestSuiteGenerator 
    implements ITestSuiteGenerator
{
    private IArgNamesProvider _argNamesProvider;
    
    protected AbstractTestSuiteGenerator()
    {
    }
    
    protected abstract void initTestSuite(Class<?> clazz, TestSuiteBuilder builder);
    
    protected abstract void addTestCases(Method method, List<Call> calls, TestSuiteBuilder builder);
    
    
    /**
     * @see com.github.sdarioo.testgen.generator.ITestSuiteGenerator#setArgNamesProvider(com.github.sdarioo.testgen.recorder.IArgNamesProvider)
     */
    @Override
    public void setArgNamesProvider(IArgNamesProvider argNamesProvider) 
    {
        _argNamesProvider = argNamesProvider;
    }
    
    /**
     * @see com.github.sdarioo.testgen.generator.ITestSuiteGenerator#generate(java.lang.Class, java.util.List)
     */
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
    
    protected String[] getParameterNames(Method method)
    {
        String[] names = null;
        if (_argNamesProvider != null) {
            names = _argNamesProvider.getArgumentNames(method);
        }
        if (names == null) {
            int size = method.getParameterTypes().length;
            names = new String[size];
            for (int i = 0; i < size; i++) {
                names[i] = "arg" + i; //$NON-NLS-1$
            }
        }
        return names;
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
