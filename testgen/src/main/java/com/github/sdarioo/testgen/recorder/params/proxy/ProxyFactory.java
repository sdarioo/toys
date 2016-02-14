package com.github.sdarioo.testgen.recorder.params.proxy;

import java.lang.reflect.Proxy;


public class ProxyFactory 
{
    
    public static boolean isProxy(Object value)
    {
        return Proxy.isProxyClass(value.getClass());
    }
    
    public static Object newProxy(Class<?> type, Object value)
    {
        Object proxy = Proxy.newProxyInstance(value.getClass().getClassLoader(), 
                new Class<?>[]{ type }, 
                new RecordingInvocationHandler(type, value));
    
        return proxy;
    }
}
