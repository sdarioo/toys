/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.math.NumberUtils;
import org.objectweb.asm.Type;

import com.github.sdarioo.testgen.recorder.params.ParamsFactory;
import com.github.sdarioo.testgen.recorder.params.ParamsUtil;

public class Call implements Comparable<Call> 
{
    private final Method _method;
    private final Class<?> _targetClass;
    
    private final long _callId;
    private final List<IParameter> _args;
    
    private IParameter _result;
    private ExceptionInfo _exception;
    
    private static final AtomicLong _callIdGenerator = new AtomicLong(0);
    
    public static Call newCall(Method method, Object... args)
    {
        return newCall(method, null, args);
    }
    
    public static Call newCall(Method method, Object target, Object[] args)
    {
        List<IParameter> params = new ArrayList<IParameter>(args.length);
        for (Object arg : args) {
            params.add(ParamsFactory.newValue(arg));
        }
        return new Call(method, target, params);
    }
    
    public static Call newCall(MethodRef ref, Object... args)
    {
        return newCall(ref, null, args);
    }
    
    public static Call newCall(MethodRef ref, Object target, Object[] args)
    {
        Method method = ref.getClass().getEnclosingMethod();
        if (method == null) {
            throw new IllegalArgumentException("MethodRef must be anonymous class within a tested method"); //$NON-NLS-1$
        }
        return newCall(method, target, args);
    }
    
    private Call(Method method, Object target, List<IParameter> args)
    {
        _method = method;
        _targetClass = (target != null) ? target.getClass() : null;
        _args = args;
        _callId = _callIdGenerator.incrementAndGet();
    }
    
    public void end()
    {
        _result = IParameter.VOID;
    }
    
    public void endWithResult(Object result)
    {
        _result = ParamsFactory.newValue(result);
    }
    
    public void endWithException(Throwable thr)
    {
        _exception = new ExceptionInfo(thr);
    }
    
    
    public Method getMethod()
    {
        return _method;
    }
    
    public boolean isFinished()
    {
        return (_result != null) || (_exception != null);
    }
    
    public boolean isStatic()
    {
        return (_method.getModifiers() & Modifier.STATIC) == Modifier.STATIC; 
    }
    
    public boolean isSupported(Set<String> errors)
    {
        if (!ParamsUtil.isSupported(_args, errors)) {
            return false;
        }
        if ((_result != null) && !_result.isSupported(errors)) {
            return false;
        }
        
        return true;
    }
    
    public Class<?> getTargetClass()
    {
        return _targetClass;
    }
    
    public List<IParameter> args()
    {
        return Collections.unmodifiableList(_args);
    }

    public IParameter getResult()
    {
        return _result;
    }
    
    public ExceptionInfo getExceptionInfo()
    {
        return _exception;
    }
    
    @Override
    public int compareTo(Call other) 
    {
        return NumberUtils.compare(_callId, other._callId);
    }
    
    @Override
    public int hashCode() 
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + _args.hashCode();
        result = prime * result
                + ((_exception == null) ? 0 : _exception.hashCode());
        result = prime * result 
                + ((_result == null) ? 0 : _result.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Call other = (Call) obj;
        if (!_args.equals(other._args)) {
            return false;
        }
        if (_exception == null) {
            if (other._exception != null)
                return false;
        } else if (!_exception.equals(other._exception))
            return false;
        if (_result == null) {
            if (other._result != null)
                return false;
        } else if (!_result.equals(other._result))
            return false;
        return true;
    }

    @Override
    public String toString() 
    {
        return _method.toString();
    }
    
    public static abstract class MethodRef {}
    
    
    public static final String TYPE_NAME = Type.getType(Call.class).getInternalName();
    public static final String NEW_CALL_METHOD_NAME = "newCall"; //$NON-NLS-1$
    public static final String NEW_CALL_METHOD_DESC = "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Lcom/github/sdarioo/testgen/recorder/Call;"; //$NON-NLS-1$
    public static final String NEW_STATIC_CALL_METHOD_DESC = "(Ljava/lang/reflect/Method;[Ljava/lang/Object;)Lcom/github/sdarioo/testgen/recorder/Call;"; //$NON-NLS-1$
    
}
