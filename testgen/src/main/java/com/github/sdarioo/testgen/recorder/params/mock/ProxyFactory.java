package com.github.sdarioo.testgen.recorder.params.mock;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.util.TypeUtil;


public class ProxyFactory 
{
 
    // IdentityHashCode -> Proxy instance
    private static ConcurrentMap<Integer, Object> CACHED_PROXIES = new ConcurrentHashMap<Integer, Object>();
    
    public static boolean canProxy(Type type, Object value)
    {
        Class<?> rawType = TypeUtil.getRawType(type);
        if (rawType == null) {
            return false;
        }
        if (value == null) {
            return false;
        }
        if (Modifier.isPrivate(rawType.getModifiers())) {
            return false;
        }
        // List<Proxy>
        if (List.class.equals(rawType)) {
            Type[] elementType = TypeUtil.getActualTypeArguments(type);
            if (elementType.length == 1) {
               return canProxyList((List<?>)value, elementType[0]);
            }
            return false;
        }
        // Proxy[]
        if (rawType.isArray()) {
            Class<?> elementType = rawType.getComponentType();
            return canProxyArray(value, elementType);
        }
        if (!rawType.isInterface()) {
            return false;
        }
        String typeName = rawType.getName();
        if (!Configuration.getDefault().isMockingEnabled(typeName)) {
            return false;
        }
        return true;
    }
    
    private static boolean canProxyList(List<?> list, Type elementType)
    {
        if (list.size() > Configuration.getDefault().getMaxCollectionSize()) {
            return false;
        }
        for (Object object : list) {
            if (!canProxy(elementType, object)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean canProxyArray(Object array, Type elementType)
    {
        if (TypeUtil.getRawType(elementType) == null) {
            return false;
        }
        int length = Array.getLength(array);
        if (length > Configuration.getDefault().getMaxCollectionSize()) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            Object element = Array.get(array, i);
            if (!canProxy(elementType, element)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isProxy(Object value)
    {
        return Proxy.isProxyClass(value.getClass()) &&
                (Proxy.getInvocationHandler(value) instanceof RecordingInvocationHandler);
    }
    
    public static RecordingInvocationHandler getHandler(Object proxy)
    {
        return (RecordingInvocationHandler)Proxy.getInvocationHandler(proxy);
    }
    
    public static Object newProxy(Type type, Object value)
    {
        if (!canProxy(type, value)) {
            return null;
        }
        Class<?> rawType = TypeUtil.getRawType(type);
        
        // List<Proxy> 
        if (List.class.equals(rawType)) {
            Type elementType = TypeUtil.getActualTypeArguments(type)[0];
            List<?> list = (List<?>)value;
            List<Object> newList = new ArrayList<Object>();
            for (Object element : list) {
                newList.add(newProxy(elementType, element));
            }
            return newList;
        }
        // Proxy[]
        if (rawType.isArray()) {
            int length = Array.getLength(value);
            Class<?> elementType = rawType.getComponentType();
            Object newArray = Array.newInstance(elementType, length);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(value, i);
                Array.set(newArray, i, newProxy(elementType, element));
            }
            return newArray;
        }
        
        Integer objectHash = System.identityHashCode(value);
        Object proxy = getFromCache(objectHash);
        
        if (proxy == null) {
            Class<?> proxyInterface = rawType;
            Class<?>[] interfaces = value.getClass().getInterfaces();
            for (Class<?> interfce : interfaces) {
                if (proxyInterface.isAssignableFrom(interfce)) {
                    proxyInterface = interfce;
                    break;
                }
            }
            proxy = Proxy.newProxyInstance(value.getClass().getClassLoader(), 
                    new Class<?>[]{ proxyInterface }, 
                    new RecordingInvocationHandler(proxyInterface, value));
            
            proxy = addToCache(objectHash, proxy);
        }
        
        ((RecordingInvocationHandler)Proxy.getInvocationHandler(proxy)).incRefCount();
        return proxy;
    }
    
    private static Object getFromCache(Integer hash)
    {
        return CACHED_PROXIES.get(hash);
    }
    
    private static Object addToCache(Integer hash, Object proxy)
    {
        Object other = CACHED_PROXIES.putIfAbsent(hash, proxy);
        return (other != null) ? other : proxy;
    }
    
}
