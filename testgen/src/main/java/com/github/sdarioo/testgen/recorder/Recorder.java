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
import java.util.concurrent.atomic.AtomicLong;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.logging.Logger;

// ThreadSafe
public final class Recorder
{
    private static final Recorder DEFAULT = new Recorder("Default"); //$NON-NLS-1$
    
    private final Map<Method, Set<Call>> _calls = new HashMap<Method, Set<Call>>();
    private final Map<Method, Set<Call>> _unsupportedCalls = new HashMap<Method, Set<Call>>();
    
    private AtomicLong _timestamp = new AtomicLong(0L);

    private final String _name;
    
    private Recorder(String name)
    {
        _name = name;
    }
    
    public static Recorder getDefault()
    {
        return DEFAULT;
    }
    
    public static Recorder newRecorder(String name)
    {
        return new Recorder(name);
    }
    
    public boolean record(Call call)
    {
        if (!call.isFinished()) {
            Logger.error("Cannot record call without return value or thrown exception: " + call.getMethod().toString()); //$NON-NLS-1$
            return false;
        }
        Method method = call.getMethod();
        if (method == null) {
            Logger.error("Cannot record call without java.lang.reflect.Method object."); //$NON-NLS-1$
            return false;
        }
        if (!call.isStatic() && (call.getTargetClass() == null)) {
            Logger.error("Missing target class for non-static method."); //$NON-NLS-1$
            return false;
        }
        if (call.args().size() != call.getMethod().getParameterTypes().length) {
            Logger.error(MessageFormat.format("Recorded call args count {0} is different that method parameters count {1}",  //$NON-NLS-1$
                    call.args().size(), call.getMethod().getParameterTypes().length));
            return false;
        }
        if (call.isSupported(new HashSet<String>())) {
            return recordCall(_calls, call);
        }
        if (count(method, _calls) < Configuration.getDefault().getMaxCalls()) {
            // Don't record unsupported calls if we have enough supported ones
            return recordCall(_unsupportedCalls, call);
        }
        return false;
    }
    
    public int getCount(Method method)
    {
        return count(method, _calls) + count(method, _unsupportedCalls);
    }

    public Collection<Class<?>> getRecordedClasses()
    {
        Set<Class<?>> result = new HashSet<Class<?>>();
        collectClasses(_calls, result);
        collectClasses(_unsupportedCalls, result);
        return result;
    }
    
    public List<Call> getCalls(Class<?> clazz)
    {
        int maxCalls = Configuration.getDefault().getMaxCalls();
        // First add valid calls. If number of valid calls is less than max number of calls
        // then return unsupported calls (info about it can be added to generated test suite).
        List<Call> result = new ArrayList<Call>();
        collectCalls(_calls, clazz, result);
        if (result.size() < maxCalls) {
            collectCalls(_unsupportedCalls, clazz, result);
        }
        
        Collections.sort(result);
        return result;
    }
    
    /**
     * @return last record timestamp, 0L if nothing has been recorded yet.
     */
    public long getTimestamp()
    {
        return _timestamp.get();
    }
    
    private static int count(Method method, Map<Method, Set<Call>> calls)
    {
        synchronized (calls) {
            Set<Call> methodCalls = calls.get(method);
            return (methodCalls != null) ? methodCalls.size() : 0;
        }
    }
    
    private boolean recordCall(Map<Method, Set<Call>> calls, Call call)
    {
        Method method = call.getMethod();
        int maxCalls = Configuration.getDefault().getMaxCalls();
        
        synchronized (calls) {
            Set<Call> methodCalls = calls.get(method);
            if (methodCalls == null) {
                methodCalls = new HashSet<Call>();
                calls.put(method, methodCalls);
            }
            if (methodCalls.size() >= maxCalls) {
                return false;
            }
            if (methodCalls.add(call)) {
                _timestamp.set(System.currentTimeMillis());
                logCall(call);
            }
            return true;
        }
    }
    
    private static void collectClasses(Map<Method, Set<Call>> calls, Set<Class<?>> result)
    {
        synchronized (calls) {
            
            for (Set<Call> methodCalls : calls.values()) {
                for (Call call : methodCalls) {
                    Method method = call.getMethod();
                    Class<?> clazz = call.isStatic() ? method.getDeclaringClass() : call.getTargetClass();
                    result.add(clazz);
                }
            }
        }
    }
    
    private static void collectCalls(Map<Method, Set<Call>> calls, Class<?> clazz, List<Call> result)
    {
        synchronized (calls) {
            
            for (Map.Entry<Method, Set<Call>> entry : calls.entrySet()) {
                Method method = entry.getKey();
                for (Call call : entry.getValue()) {
                    Class<?> callClass = call.isStatic() ? method.getDeclaringClass() : call.getTargetClass();
                    if (clazz.equals(callClass)) {
                        result.add(call);
                    }
                }
            }
        }
    }
    
    @SuppressWarnings("nls")
    private void logCall(Call call)
    {
        Set<String> errors = new HashSet<String>();
        boolean isSupported = call.isSupported(errors);
        
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format("[{0}] Recording {1} call: {2}", _name, (isSupported ? "supported" : "unsupported"), call));
        if (call.getExceptionInfo() != null) {
            sb.append("\n   Expected exception=").append(call.getExceptionInfo().getClassName());
        }
        
        for (String msg : errors) {
            sb.append("\n   " + msg); //$NON-NLS-1$
        }
        if (isSupported) {
            Logger.info(sb.toString());
        } else {
            Logger.warn(sb.toString());
        }
    }
   
}
