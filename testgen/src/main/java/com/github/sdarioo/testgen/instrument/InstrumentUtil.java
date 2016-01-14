package com.github.sdarioo.testgen.instrument;

import java.lang.reflect.Method;

public final class InstrumentUtil 
{
    private InstrumentUtil() {}
    
    public static java.lang.reflect.Method getMethod(Class<?> owner, String name, String descriptor)
    {
        Method[] declaredMethods = owner.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (name.equals(method.getName())) {
                String desc = getDescriptor(method);
                if (descriptor.equals(desc)) {
                    return method;
                }
            }
        }
        return null;
    }
    
    public static String getDescriptor(java.lang.reflect.Method method)
    {
        return org.objectweb.asm.commons.Method.getMethod(method).getDescriptor();
    }
    
    public static boolean isFlagSet(int access, int... flags)
    {
        for (int flag : flags) {
            if ((access & flag) == flag) {
                return true;
            }
        }
        return false;
    }
}
