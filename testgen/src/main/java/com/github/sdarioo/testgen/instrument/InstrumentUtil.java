package com.github.sdarioo.testgen.instrument;

import org.objectweb.asm.Type;

public final class InstrumentUtil 
{
    private InstrumentUtil() {}

    public static boolean isFlagSet(int access, int... flags)
    {
        for (int flag : flags) {
            if ((access & flag) == flag) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isPrimitive(Type t)
    {
        return t.getSort() <= 8;
    }
    
    
}
