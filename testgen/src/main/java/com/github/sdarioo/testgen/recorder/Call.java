/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.lang.reflect.Method;
import java.util.Set;

public class Call 
{
    private final Method _method;
    
    private IParameter _result;
    private ExceptionInfo _exception;
    private final ArgList _argList;
    
    
    public static Call newCall(MethodRef ref)
    {
        Method method = ref.getClass().getEnclosingMethod();
        if (method == null) {
            throw new IllegalArgumentException("MethodRef must be anonymous class within a tested method"); //$NON-NLS-1$
        }
        return new Call(method);
    }
    
    public static Call newCall(Method method)
    {
        return new Call(method);
    }
    
    private Call(Method method)
    {
        _method = method;
        _argList = new ArgList();
    }
    
    public Call(Method method, IParameter result, IParameter... args)
    {
        _method = method;
        _argList = new ArgList();
        setResult(result);
        for (IParameter value : args) {
            args().add(value);
        }
    }
    
    public Method getMethod()
    {
        return _method;
    }
    
    public boolean isFinished()
    {
        // Void methods must have IParameter.VOID set as result
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
    
    public void setResult(IParameter result)
    {
        _result = result;
    }
    
    public ExceptionInfo getExceptionInfo()
    {
        return _exception;
    }
    
    public void setException(Throwable exception)
    {
        _exception = new ExceptionInfo(exception);
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

    
    public static abstract class MethodRef {}
}
