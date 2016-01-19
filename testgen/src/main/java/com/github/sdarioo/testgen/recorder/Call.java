/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.lang.reflect.Method;
import java.util.Set;

import org.objectweb.asm.Type;

import com.github.sdarioo.testgen.recorder.params.ParamsFactory;

public class Call 
{
    private final Method _method;
    
    private IParameter _result;
    private ExceptionInfo _exception;
    private final ArgList _argList;
    
    public static Call newCall(Method method, Object... args)
    {
        IParameter[] params = new IParameter[args.length];
        for (int i = 0; i < args.length; i++) {
            params[i] = ParamsFactory.newValue(args[i]);
        }
        return new Call(method, params);
    }
    
    public static Call newCall(MethodRef ref, Object... args)
    {
        Method method = ref.getClass().getEnclosingMethod();
        if (method == null) {
            throw new IllegalArgumentException("MethodRef must be anonymous class within a tested method"); //$NON-NLS-1$
        }
        return newCall(method, args);
    }
    
    private Call(Method method, IParameter[] args)
    {
        _method = method;
        _argList = new ArgList(args);
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
    
    public boolean isSupported(Set<String> errors)
    {
        if (!args().isSupported(errors)) {
            return false;
        }
        if ((_result != null) && !_result.isSupported(errors)) {
            return false;
        }
        return true;
    }
    
    public ArgList args()
    {
        return _argList;
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
    public int hashCode() 
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((_argList == null) ? 0 : _argList.hashCode());
        result = prime * result
                + ((_exception == null) ? 0 : _exception.hashCode());
        result = prime * result + ((_result == null) ? 0 : _result.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Call other = (Call) obj;
        if (_argList == null) {
            if (other._argList != null)
                return false;
        } else if (!_argList.equals(other._argList))
            return false;
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
    public static final String NEW_CALL_METHOD_DESC = "(Ljava/lang/reflect/Method;[Ljava/lang/Object;)Lcom/github/sdarioo/testgen/recorder/Call;"; //$NON-NLS-1$
    
    
}
