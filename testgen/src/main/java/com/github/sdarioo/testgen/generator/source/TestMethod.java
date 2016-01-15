/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.source;

import org.apache.commons.lang3.math.NumberUtils;

public class TestMethod 
    implements Comparable<TestMethod>
{
    private final String _name;
    private final String _source;
    private final int _order;
    
    public TestMethod(String name, String source)
    {
        this(name, source, DEFAULT_ORDER);
    }
    
    public TestMethod(String name, String source, int order)
    {
        _name = name;
        _source = source;
        _order = order;
    }
    
    public String getName()
    {
        return _name;
    }
    
    public String toSourceCode()
    {
        return _source;
    }
    
    @Override
    public int compareTo(TestMethod m) 
    {
        return NumberUtils.compare(_order, m._order);
    }
    
    public static final int DEFAULT_ORDER = 100;
}
