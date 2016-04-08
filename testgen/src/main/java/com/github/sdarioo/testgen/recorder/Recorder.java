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

import com.github.sdarioo.testgen.logging.Logger;

// ThreadSafe
public final class Recorder
{
    private final String _name;
    private final ConcurrentMap<Class<?>, RecordedClass> _recordedClasses = new ConcurrentHashMap<>();
    
    private static final Recorder DEFAULT = new Recorder("Default"); //$NON-NLS-1$

    
    public static Recorder getDefault()
    {
        return DEFAULT;
    }
    public static Recorder newRecorder(String name)
    {
        return new Recorder(name);
    }
    
    private Recorder(String name)
    {
        _name = name;
    }
    
    public boolean record(Call call)
    {
        RecordedClass recordedClass = getRecordedClass(call, true);
        if (recordedClass.record(call)) {
            logCall(call);
            return true;
        }
        return false;
    }

    public Collection<RecordedClass> getRecordedClasses()
    {
        return _recordedClasses.values();
    }
    
    public RecordedClass getRecordedClass(Class<?> clazz)
    {
        return _recordedClasses.get(clazz);
    }

    private RecordedClass getRecordedClass(Call call, boolean bCreate)
    {
        Method method = call.getMethod();
        Class<?> clazz = call.isStatic() ?  method.getDeclaringClass() : call.getTargetClass();
        
        RecordedClass recordedClass = _recordedClasses.get(clazz);
        if ((recordedClass == null) && bCreate) {
            recordedClass = new RecordedClass(clazz);
            RecordedClass other = _recordedClasses.putIfAbsent(clazz, recordedClass);
            if (other != null) {
                recordedClass = other;
            }
        }
        return recordedClass;
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
