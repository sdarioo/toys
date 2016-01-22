/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.github.sdarioo.testgen.recorder.IParameter;

public final class ParamsUtil 
{
    private ParamsUtil() {}
    
    public static boolean isSupported(List<IParameter> params, Collection<String> errors)
    {
        boolean bValid = true;
        for (IParameter param : params) {
            if (!param.isSupported(errors)) {
                bValid = false;
            }
        }
        return bValid;
    }
    
    public static boolean isSupported(IParameter[] params, Collection<String> errors)
    {
        return isSupported(Arrays.asList(params), errors);
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
