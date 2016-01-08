/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.lang.reflect.Method;
import java.util.*;

public final class Recorder 
{
    private static final Recorder DEFAULT = new Recorder();
    
    private final Map<Method, Set<Call>> _calls = new HashMap<Method, Set<Call>>();
    
    private Recorder() {}
    
    public static Recorder getDefault()
    {
        return DEFAULT;
    }
    
    public void record(Call call)
    {
        if (!call.isFinished()) {
            return;
        }
        Method method = call.getMethod();
        int maxCalls = maxCalls(method);
        
        synchronized (_calls) {
            Set<Call> calls = _calls.get(method);
            if (calls == null) {
                calls = new HashSet<Call>();
                _calls.put(method, calls);
            }
            if (calls.size() >= maxCalls) {
                return;
            }
            calls.add(call);
        }
    }
    
    public Collection<Class<?>> getRecordedClasses()
    {
        Set<Class<?>> result = new HashSet<Class<?>>();
        for (Method method : _calls.keySet()) {
            result.add(method.getDeclaringClass());
        }
        return result;
    }
    
    public List<Call> getCalls(Class<?> clazz)
    {
        List<Call> result = new ArrayList<Call>();
        for (Map.Entry<Method, Set<Call>> entry : _calls.entrySet()) {
            Method method = entry.getKey();
            if (clazz.equals(method.getDeclaringClass())) {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }
    
    private static int maxCalls(Method method)
    {
        RuntimeTestGeneration annot = method.getAnnotation(RuntimeTestGeneration.class);
        if (annot != null) {
            return annot.maxCalls();
        }
        return DEFAULT_MAX_CALLS;
    }
    
    public static final int DEFAULT_MAX_CALLS = 10;
}
