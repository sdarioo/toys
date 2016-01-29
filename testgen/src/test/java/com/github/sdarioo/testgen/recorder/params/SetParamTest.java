/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class SetParamTest 
{
    
    @Test
    public void testSupportedType() throws Exception
    {
        
    }
    
    
    public void foo(Set<String> set)
    {
    }
    public static <T> void foo1(Set<T> set)
    {
    }
    public void foo2(Set<String[]> list)
    {
    }
    public void foo3(TreeSet set)
    {
    }
}
