/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.source;

public class TestMethod 
{
    private final String _name;
    private final String _source;
    
    public TestMethod(String name, String source)
    {
        _name = name;
        _source = source;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public String toSourceCode()
    {
        return _source;
    }
    
}
