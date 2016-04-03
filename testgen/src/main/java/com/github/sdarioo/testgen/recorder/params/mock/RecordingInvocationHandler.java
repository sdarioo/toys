package com.github.sdarioo.testgen.recorder.params.mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.Recorder;

/**
 * Proxy invocation handler. 
 * Each proxy instance has separate instance of invocation handler.
 */
public class RecordingInvocationHandler
    implements InvocationHandler
{
    private final Class<?> _interface;
    private final Object _original;
    private final Recorder _recorder;
    
    private boolean _isAllRecorded = true;
    private String _exception;
    private int _refCount = 0;
    
    private static final AtomicInteger _idGenerator = new AtomicInteger(0);
    
    public RecordingInvocationHandler(Class<?> interfce, Object original)
    {
        _interface = interfce;
        _original = original;
        _recorder = Recorder.newRecorder("MockRecorder-" + _idGenerator.incrementAndGet()); //$NON-NLS-1$
    }
    
    public Class<?> getInterface() 
    {
        return _interface;
    }
    
    public int getRefCount()
    {
        return _refCount;
    }
    
    public void incRefCount()
    {
        _refCount++;
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) 
        throws Throwable 
    {
        if (args == null) {
            args = new Object[0];
        }
        
        Call call = Call.newCall(method, _original, args);
        try {
            Object result = method.invoke(_original, args);
            Type returnType = method.getGenericReturnType();
            if (returnType != Void.TYPE) {
                if (result != null) {
                    if (ProxyFactory.canProxy(returnType, result)) {
                        result = ProxyFactory.newProxy(returnType, result);
                    }
                }   
                
                // If proxy is passed as argument into another proxy we must increment its reference count, otherwise
                // it will be treated as other object in generated code
                for (Object arg : args) {
                    if (ProxyFactory.isProxy(arg)) {
                        ProxyFactory.getHandler(arg).incRefCount();
                    }
                }
                
                call.endWithResult(result);
                _isAllRecorded &= _recorder.record(call);
            } else {
                Logger.warn("Ignoring void method call on proxy: " + method.toGenericString()); //$NON-NLS-1$
                call.end();
            }
            return result;
        } catch (Throwable thr) {
            Throwable cause = thr;
            if (thr instanceof InvocationTargetException) {
                cause = ((InvocationTargetException)thr).getCause();
            }
            Logger.warn("Exception thrown in RecordingInvocationHandler.invoke: " + cause); //$NON-NLS-1$
            _exception = cause.toString();
            call.endWithException(cause);
            throw cause;
        }
    }
    
    public boolean isSupported(Collection<String> errors)
    {
        if (_exception != null) {
            errors.add("Proxy method exited with exception: " + _exception); //$NON-NLS-1$
            return false;
        }

        List<Call> calls = getCalls();
        if (!_isAllRecorded) {
            errors.add("Some method call could not be recorded."); //$NON-NLS-1$
            return false;
        }
        boolean bAllSupported = true;
        for (Call call : calls) {
            boolean bCallSupported = call.isSupported(errors);
            bAllSupported &= bCallSupported;
        }
        return bAllSupported;
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
