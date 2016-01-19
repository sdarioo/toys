/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.util.*;

import com.github.sdarioo.testgen.recorder.params.ParamsUtil;

public final class ArgList
{
    private final IParameter[] _args;
    
    ArgList(IParameter[] args)
    {
        _args = args;
    }
    
    public int size()
    {
        return _args.length;
    }
    
    public IParameter get(int index)
    {
        return _args[index];
    }

    public List<IParameter> getValues() 
    {
        return Arrays.asList(_args);
    }
    
    public boolean isSupported(Collection<String> errors)
    {
        return ParamsUtil.isSupported(_args, errors);
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof ArgList)) {
            return false;
        }
        ArgList other = (ArgList)obj;
        return ParamsUtil.equals(_args, other._args);
    }
    
    @Override
    public int hashCode() 
    {
        return ParamsUtil.hashCode(_args);
    }

}
