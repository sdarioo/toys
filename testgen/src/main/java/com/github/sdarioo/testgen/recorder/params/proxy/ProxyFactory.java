package com.github.sdarioo.testgen.recorder.params.proxy;

import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import com.github.sdarioo.testgen.util.TypeUtil;


public class ProxyFactory 
{
    
    public static boolean canProxy(Type argType, Object argValue)
    {
        Class<?> rawType = TypeUtil.getRawType(argType);
        if (rawType == null) {
            return false;
        }
        if (argValue == null) {
            return false;
        }
        if (!rawType.isInterface()) {
            return false;
        }
        String typeName = rawType.getName();
        if ((typeName == null) || typeName.startsWith("java")) {
            return false;
        }
        if (!Modifier.isPublic(rawType.getModifiers())) {
            return false;
        }
        return true;
    }
    
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
