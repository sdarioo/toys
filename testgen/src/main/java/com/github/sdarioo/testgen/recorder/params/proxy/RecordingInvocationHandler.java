package com.github.sdarioo.testgen.recorder.params.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.Recorder;

public class RecordingInvocationHandler
    implements InvocationHandler
{
    private final Class<?> _type;
    private final Object _original;
    private final Recorder _recorder;
    
    public RecordingInvocationHandler(Class<?> type, Object original)
    {
        _type = type;
        _original = original;
        _recorder = Recorder.newRecorder();
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) 
            throws Throwable 
    {
        
        Call call = Call.newCall(method, _original, (args != null ? args : new Object[0]));
        
        try {
            Object result = method.invoke(_original, args);
            if (method.getReturnType() != Void.TYPE) {
                call.endWithResult(result);
                _recorder.record(call);
            }
            return result;
        } catch (Throwable thr) {
            throw thr;
        }
    }

}
