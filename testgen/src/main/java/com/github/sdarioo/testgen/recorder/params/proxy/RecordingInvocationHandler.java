package com.github.sdarioo.testgen.recorder.params.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.Recorder;

public class RecordingInvocationHandler
    implements InvocationHandler
{
    private final Class<?> _interface;
    private final Object _original;
    private final Recorder _recorder;
    
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
            if (method.getReturnType() != Void.TYPE) {
                
                // TODO: Proxy-result if needed
                
                call.endWithResult(result);
                _recorder.record(call);
            }
            return result;
        } catch (Throwable thr) {
            throw thr;
        }
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
