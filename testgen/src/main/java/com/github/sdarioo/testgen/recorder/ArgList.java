/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.util.*;

public final class ArgList
{
    private final List<IParameter> _args = new ArrayList<IParameter>();
    
    ArgList()
    {
    }
    
    public int size()
    {
        return _args.size();
    }
    
    public IParameter get(int index)
    {
        return _args.get(index);
    }
    
    public void add(IParameter value)
    {
        _args.add(value);
    }
    
    public List<IParameter> getValues() 
    {
        return Collections.unmodifiableList(_args);
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof ArgList)) {
            return false;
        }
        ArgList other = (ArgList)obj;
        return _args.equals(other._args);
    }
    
    @Override
    public int hashCode() 
    {
        return _args.hashCode();
    }

}
