package com.github.sdarioo.testgen.recorder.params.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.recorder.params.AbstractParam;

public class ProxyParam
    extends AbstractParam
{
    private final Object _proxy;
    
    public ProxyParam(Object proxy)
    {
        super(proxy.getClass());
        _proxy = proxy;
    }

    @Override
    public boolean isSupported(Type targetType, Collection<String> errors) 
    {
        InvocationHandler handler = Proxy.getInvocationHandler(_proxy);
        if (!(handler instanceof RecordingInvocationHandler)) {
            return false;
        }
        RecordingInvocationHandler recordingHandler = (RecordingInvocationHandler)handler;
        errors.add("Proxy not supported yet.");
        return false;
    }

    @Override
    public String toSouceCode(Type targetType, TestSuiteBuilder builder) 
    {
        RecordingInvocationHandler recordingHandler = 
                (RecordingInvocationHandler)Proxy.getInvocationHandler(_proxy);
        
        
        return null;
    }

}
