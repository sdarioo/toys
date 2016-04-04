package com.github.sdarioo.testgen.recorder.params.mock;

import java.lang.reflect.*;
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
    
    private int _hash = 1;
    private boolean _isAllRecorded = true;
    private boolean _isAllSupported = true;
    
    private String _exception;
    private int _refCount = 0;
    
    
    private final Map<String, String> _attrs = new HashMap<String, String>();
    
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
    
    public String getAttr(String key)
    {
        return _attrs.get(key);
    }
    
    public void setAttr(String key, String value)
    {
        _attrs.put(key, value);
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) 
        throws Throwable 
    {
        if (args == null) {
            args = new Object[0];
        }
        
        Object result = null;
        try {
            result = method.invoke(_original, args);
        } catch (Throwable exception) {
            if (exception instanceof InvocationTargetException) {
                exception = ((InvocationTargetException)exception).getCause();
            }
            Logger.warn("Exception thrown in RecordingInvocationHandler.invoke: " + exception); //$NON-NLS-1$
            _exception = exception.toString();
            throw exception;
        }
        
        Call call = Call.newCall(method, _original, args);
        Type returnType = method.getGenericReturnType();
        if (returnType != Void.TYPE) {
            if (ProxyFactory.canProxy(returnType, result)) {
                result = ProxyFactory.newProxy(returnType, result);
            }
            for (Object arg : args) {
                if (ProxyFactory.isProxy(arg)) {
                    ProxyFactory.getHandler(arg).incRefCount();
                }
            }
            
            call.endWithResult(result);
            
            _hash = 31 * _hash + call.hashCode();
            _isAllSupported &= call.isSupported(new HashSet<String>());
            _isAllRecorded &= _recorder.record(call);
            
        } else {
            Logger.warn("Ignoring void method call on proxy: " + method.toGenericString()); //$NON-NLS-1$
        }
        return result;
    }
    
    public boolean isSupported(Collection<String> errors)
    {
        if (_exception != null) {
            errors.add("Proxy method exited with exception: " + _exception); //$NON-NLS-1$
            return false;
        }

        if (!_isAllRecorded) {
            errors.add("Some method call could not be recorded."); //$NON-NLS-1$
            return false;
        }
        return _isAllSupported;
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
    
    @Override
    public int hashCode() 
    {
        return _hash;
    }
    
}
