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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.logging.Logger;

// ThreadSafe
public final class Recorder
    implements IArgNamesProvider
{
    private static final Recorder DEFAULT = new Recorder();
    private static final ConcurrentMap<String, Recorder> RECORDERS = new ConcurrentHashMap<String, Recorder>();
    
    private static final Map<String, String[]> ARG_NAMES = new HashMap<String, String[]>();
    
    private final Map<Method, Set<Call>> _calls = new HashMap<Method, Set<Call>>();
    private final Map<Method, Set<Call>> _unsupportedCalls = new HashMap<Method, Set<Call>>();
    
    private AtomicLong _timestamp = new AtomicLong(0L);

    private Recorder() {}
    
    public static Recorder getDefault()
    {
        return DEFAULT;
    }
    
    public static Recorder get(String key)
    {
        Recorder recorder = RECORDERS.get(key);
        if (recorder == null) {
            Recorder newRecorder = new Recorder();
            recorder = RECORDERS.putIfAbsent(key, newRecorder);
            if (recorder == null) {
                recorder = newRecorder;
            }
        }
        return recorder;
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
        if (!call.isStatic() && (call.getTargetClass() == null)) {
            Logger.error("Missing target class for non-static method."); //$NON-NLS-1$
            return;
        }
        if (call.args().size() != call.getMethod().getParameterTypes().length) {
            Logger.error(MessageFormat.format("Recorded call args count {0} is different that method parameters count {1}",  //$NON-NLS-1$
                    call.args().size(), call.getMethod().getParameterTypes().length));
            return;
        }
        
        if (call.isSupported(new HashSet<String>())) {
            recordCall(_calls, call);
        } else {
            recordCall(_unsupportedCalls, call);
        }
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
    
    /**
     * @see com.github.sdarioo.testgen.recorder.IArgNamesProvider#getArgumentNames(java.lang.reflect.Method)
     */
    @Override
    public String[] getArgumentNames(Method method) 
    {
        String typeDesc = org.objectweb.asm.Type.getDescriptor(method.getDeclaringClass());
        String methodDesc = org.objectweb.asm.Type.getMethodDescriptor(method);
        String key = typeDesc + '-' + methodDesc;
        return ARG_NAMES.get(key);
    }
    
    public void setArgumentNames(org.objectweb.asm.Type type, org.objectweb.asm.commons.Method method, String[] names)
    {
        if (names != null) {
            for (String name : names) {
                if (name == null) {
                    return;
                }
            }
            String key = type.getDescriptor() + '-' + method.getDescriptor();
            ARG_NAMES.put(key, names);
        }
    }
    
    private void recordCall(Map<Method, Set<Call>> calls, Call call)
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
                if (methodCalls.add(call)) {
                    _timestamp.set(System.currentTimeMillis());
                    logCall(call);
                }
            }
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
    private static void logCall(Call call)
    {
        Set<String> errors = new HashSet<String>();
        boolean isSupported = call.isSupported(errors);
        
        StringBuilder sb = new StringBuilder();
        sb.append(MessageFormat.format("Recording {0} call: {1}", (isSupported ? "supported" : "unsupported"), call));
        
        for (String msg : errors) {
            sb.append("\n   " + msg); //$NON-NLS-1$
        }
        Logger.info(sb.toString());
    }
   
}
