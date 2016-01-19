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

import org.objectweb.asm.Type;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.logging.Logger;

public final class Recorder
    implements IArgNamesProvider
{
    private static final Recorder DEFAULT = new Recorder();
    private static final ConcurrentMap<String, Recorder> RECORDERS = new ConcurrentHashMap<String, Recorder>();
    
    private static final Map<String, String[]> ARG_NAMES = new HashMap<String, String[]>();
    
    private final Map<Method, Set<Call>> _calls = new HashMap<Method, Set<Call>>();
    private final Map<Method, Set<Call>> _unsupportedCalls = new HashMap<Method, Set<Call>>();
    
    
    public static final String TYPE_NAME = Type.getType(Recorder.class).getInternalName();
    public static final String GET_DEFAULT_METHOD_NAME = "getDefault"; //$NON-NLS-1$
    public static final String GET_DEFAULT_METHOD_DESC = "()Lcom/github/sdarioo/testgen/recorder/Recorder;"; //$NON-NLS-1$
    public static final String RECORD_METHOD_NAME = "record"; //$NON-NLS-1$
    public static final String RECORD_METHOD_DESC = "(Lcom/github/sdarioo/testgen/recorder/Call;)V"; //$NON-NLS-1$
    
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
        return result;
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
                if (methodCalls.add(call)) {
                    Logger.info(MessageFormat.format("Recording {0} call: {1}",  //$NON-NLS-1$
                            call.isSupported(new HashSet<String>()) ? "supported" : "unsupported", call)); //$NON-NLS-1$ //$NON-NLS-2$
                }
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
