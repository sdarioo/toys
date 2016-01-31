/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.IParameter;

public final class ParamsUtil 
{
    private ParamsUtil() {}
    
    
    public static String getRawTypeName(Type type)
    {
        Class<?> rawType = getRawType(type);
        return (rawType != null) ? rawType.getName() : type.getClass().getName();
    }
    
    public static Class<?> getRawType(Type type)
    {
        if (type == null) {
            return null;
        }
        if (type instanceof Class<?>) {
            return (Class<?>)type;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType)type).getRawType();
            return getRawType(rawType);
        }
        
        Logger.warn("Cannot get raw type from: " + type.getClass().getName()); //$NON-NLS-1$
        return null;
    }
    
    public static boolean isSupported(Collection<IParameter> params, Collection<String> errors)
    {
        boolean bValid = true;
        for (IParameter param : params) {
            if (!param.isSupported(errors)) {
                bValid = false;
            }
        }
        return bValid;
    }
    
    public static boolean isTypeCompatible(Type expectedType, Class<?> generatedClass)
    {
        Class<?> expectedRawType = ParamsUtil.getRawType(expectedType);
        if ((expectedRawType != null) && !expectedRawType.isAssignableFrom(generatedClass)) {
            return false;
        }
        return true;
    }

    public static int hashCode(IParameter[] params)
    {
        int hash = 1;
        for (IParameter param : params) {
            hash = (31 * hash) + param.hashCode();
        }
        return hash;
    }
    
    public static boolean equals(IParameter[] p1, IParameter[] p2)
    {
        if (p1 == p2) {
            return true;
        }
        if (p1.length != p2.length) {
            return false;
        }
        
        for (int i = 0; i < p1.length; i++) {
            if (!p1[i].equals(p2[i])) {
                return false;
            }
        }
        return true;
    }

    
}
