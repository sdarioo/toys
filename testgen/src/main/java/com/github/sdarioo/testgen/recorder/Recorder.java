/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.logging.Logger;

public final class Recorder 
{
    private static final Recorder DEFAULT = new Recorder();
    
    private final Map<Method, Set<Call>> _calls = new HashMap<Method, Set<Call>>();
    
    private final Map<Method, Set<Call>> _invalidCalls = new HashMap<Method, Set<Call>>();
    
    private Recorder() {}
    
    public static Recorder getDefault()
    {
        return DEFAULT;
    }
    
    public void record(Call call)
    {
        if (!call.isFinished()) {
            Logger.error("Cannot record call without return value or thrown exception: " + call.getMethod().toString()); //$NON-NLS-1$
            return;
        }
        if (call.getMethod() == null) {
            Logger.error("Cannot record call without java.lang.reflect.Method object."); //$NON-NLS-1$
            return;
        }
        if (call.args().size() != call.getMethod().getParameterTypes().length) {
            Logger.warn(MessageFormat.format("Recorded call args count {0} is different that method parameters count {1}",  //$NON-NLS-1$
                    call.args().size(), call.getMethod().getParameterTypes().length));
            return;
        }
        
        if (call.isValid()) {
            recordCall(_calls, call);
        } else {
            recordCall(_invalidCalls, call);
        }
    }

    public Collection<Class<?>> getRecordedClasses()
    {
        Set<Class<?>> result = new HashSet<Class<?>>();
        collectClasses(_calls, result);
        collectClasses(_invalidCalls, result);
        return result;
    }
    
    public List<Call> getCalls(Class<?> clazz)
    {
        int maxCalls = Configuration.getDefault().getMaxCalls();
        
        List<Call> result = new ArrayList<Call>();
        collectCalls(_calls, clazz, result);
        if (result.size() < maxCalls) {
            collectCalls(_invalidCalls, clazz, result);
        }
        return result;
    }
    
    private static void recordCall(Map<Method, Set<Call>> calls, Call call)
    {
        Method method = call.getMethod();
        int maxCalls = Configuration.getDefault().getMaxCalls();
        
        synchronized (calls) {
            Set<Call> methodCalls = calls.get(method);
            if (methodCalls == null) {
                methodCalls = new HashSet<Call>();
                calls.put(method, methodCalls);
            }
            if (methodCalls.size() < maxCalls) {
                methodCalls.add(call);
            }
        }
    }
    
    private static void collectClasses(Map<Method, Set<Call>> calls, Set<Class<?>> result)
    {
        synchronized (calls) {
            for (Method method : calls.keySet()) {
                // TODO - method from parent class??
                result.add(method.getDeclaringClass());
            }
        }
    }
    
    private static void collectCalls(Map<Method, Set<Call>> calls, Class<?> clazz, List<Call> result)
    {
        synchronized (calls) {
            for (Map.Entry<Method, Set<Call>> entry : calls.entrySet()) {
                Method method = entry.getKey();
                if (clazz.equals(method.getDeclaringClass())) {
                    result.addAll(entry.getValue());
                }
            }
        }
    }
   
}
