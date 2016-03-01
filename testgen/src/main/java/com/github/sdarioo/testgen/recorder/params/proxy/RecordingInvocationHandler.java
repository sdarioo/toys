package com.github.sdarioo.testgen.recorder.params.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.Recorder;

public class RecordingInvocationHandler
    implements InvocationHandler
{
    private final Class<?> _interface;
    private final Object _original;
    private final Recorder _recorder;
    
    private boolean _isAllRecorded = true;
    private boolean _isException;
    private boolean _isVoidCall;
    
    public RecordingInvocationHandler(Class<?> interfce, Object original)
    {
        _interface = interfce;
        _original = original;
        _recorder = Recorder.newRecorder();
    }
    
    public Class<?> getInterface() 
    {
        return _interface;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) 
        throws Throwable 
    {
        Call call = Call.newCall(method, _original, (args != null ? args : new Object[0]));
        try {
            Object result = method.invoke(_original, args);
            Class<?> returnType = method.getReturnType();
            if (returnType != Void.TYPE) {
                if (result != null) {
                    if (ProxyFactory.canProxy(returnType, result)) {
                        result = ProxyFactory.newProxy(returnType, result);
                    }
                }    
                call.endWithResult(result);
                _isAllRecorded &= _recorder.record(call);
            } else {
                _isVoidCall = true;
                call.end();
            }
            return result;
        } catch (Throwable thr) {
            _isException = true;
            call.endWithException(thr);
            throw thr;
        }
    }
    
    public boolean isSupported(Collection<String> errors)
    {
        if (_isException) {
            errors.add("Cannot mock methods that throws exceptions."); //$NON-NLS-1$
            return false;
        }
        if (_isVoidCall) {
            errors.add("Cannot mock void methods."); //$NON-NLS-1$
            return false;
        }
        List<Call> calls = getCalls();
        if (!_isAllRecorded) {
            errors.add("Some method call could not be recorded."); //$NON-NLS-1$
            return false;
        }
        boolean bSupported = true;
        for (Call call : calls) {
            bSupported &= call.isSupported(errors);
        }
        return bSupported;
    }
    
    public boolean isMultipleCallsToSameMethod()
    {
        return getCalls().size() > getMethods().size();
    }

    public Set<Method> getMethods()
    {
        Set<Method> methods = new HashSet<Method>();
        for (Call call : getCalls()) {
            methods.add(call.getMethod());
        }
        return methods;
    }
    
    public List<Call> getCalls()
    {
        Collection<Class<?>> recordedClasses = _recorder.getRecordedClasses();
        if (recordedClasses.isEmpty()) {
            return Collections.emptyList();
        }
        Class<?> clazz = recordedClasses.iterator().next();
        return _recorder.getCalls(clazz);
    }
}
