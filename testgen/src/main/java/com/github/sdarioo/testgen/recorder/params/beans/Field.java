/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params.beans;

import org.objectweb.asm.Opcodes;

public final class Field
{
    int access;
    String name;
    String desc;
    
    public Field(int access, String name, String desc)
    {
        this.access = access;
        this.name = name;
        this.desc = desc;
    }
    
    public boolean isPrivate()
    {
        return (access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE;
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public String getDesc()
    {
        return this.desc;
    }
    
    @Override
    public int hashCode() 
    {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Field)) {
            return false;
        }
        Field other = (Field)obj;
        return name.equals(other.name) && desc.equals(other.desc);
    }
}
