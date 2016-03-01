/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.io.File;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;

import com.github.sdarioo.testgen.generator.source.TestClass;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.ArgNamesCache;

public abstract class AbstractTestSuiteGenerator 
    implements ITestSuiteGenerator
{
    private File _locationDir;
    
    protected abstract void initTestSuite(Class<?> targetClass, 
            TestSuiteBuilder builder);
    
    protected abstract void addTestCases(Class<?> targetClass, 
            Method method, 
            List<Call> callsWithResult, 
            TestSuiteBuilder builder);
    
    protected abstract void addTestCasesForExceptions(Class<?> targetClass,
            Method method, 
            List<Call> callsWithException, 
            TestSuiteBuilder builder);
    
    
    /**
     * @see com.github.sdarioo.testgen.generator.ITestSuiteGenerator#setLocationDir(java.io.File)
     */
    public void setLocationDir(File locationDir)
    {
        _locationDir = locationDir;
    }
    
    /**
     * @see com.github.sdarioo.testgen.generator.ITestSuiteGenerator#generate(java.lang.Class, java.util.List)
     */
    @Override
    public TestClass generate(Class<?> targetClass, List<Call> recordedCalls)
    {
        Map<Method, List<Call>> callMap = groupByMethod(recordedCalls);
        
        TestSuiteBuilder builder = new TestSuiteBuilder(false, _locationDir);
        initTestSuite(targetClass, builder);
        
        for (Map.Entry<Method, List<Call>> entry : callMap.entrySet()) {
            Method method = entry.getKey();
            List<Call> methodCalls = entry.getValue();
            
            List<Call> callsWithResult = getCallsWithResult(methodCalls);
            if (!callsWithResult.isEmpty()) {
                addTestCases(targetClass, method, callsWithResult, builder);
            }
            List<Call> callsWithExc = getCallsWithException(methodCalls);
            if (!callsWithExc.isEmpty()) {
                addTestCasesForExceptions(targetClass, method, callsWithExc, builder);
            }
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
        return ArgNamesCache.getArgNames(method);
    }
    
    protected static String fmt(String pattern, Object... args)
    {
        return MessageFormat.format(pattern, args);
    }
    
    private static Map<Method, List<Call>> groupByMethod(List<Call> clazzCalls)
    {
        Map<Method, List<Call>> result = new LinkedHashMap<Method, List<Call>>();
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
    
    private static List<Call> getCallsWithException(List<Call> calls)
    {
        List<Call> result = new ArrayList<Call>();
        for (Call call : calls) {
            if (call.getExceptionInfo() != null) {
                result.add(call);
            }
        }
        return result;
    }
    
    private static List<Call> getCallsWithResult(List<Call> calls)
    {
        List<Call> result = new ArrayList<Call>();
        for (Call call : calls) {
            if (call.getResult() != null) {
                result.add(call);
            }
        }
        return result;
    }
}
