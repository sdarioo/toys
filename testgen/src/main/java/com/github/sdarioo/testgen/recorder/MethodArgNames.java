package com.github.sdarioo.testgen.recorder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MethodArgNames 
{
    private static ConcurrentMap<String, String[]> NAMES = new ConcurrentHashMap<String, String[]>();
    
    private MethodArgNames() {}
    
    public static String[] getArgNames(java.lang.reflect.Method method)
    {
        String typeDesc = org.objectweb.asm.Type.getDescriptor(method.getDeclaringClass());
        String methodName = method.getName();
        String methodDesc = org.objectweb.asm.Type.getMethodDescriptor(method);
        String key = getKey(typeDesc, methodName, methodDesc);
        return NAMES.get(key);
    }
    
    public static void setArgNames(org.objectweb.asm.Type type, org.objectweb.asm.commons.Method method, String[] names)
    {
        String key = getKey(type.getDescriptor(), method.getName(), method.getDescriptor());
        NAMES.put(key, names);
    }
    
    private static String getKey(String typeDesc, String methodName, String methodDesc)
    {
        return typeDesc + ':' + methodName + ':' + methodDesc;
    }
}
