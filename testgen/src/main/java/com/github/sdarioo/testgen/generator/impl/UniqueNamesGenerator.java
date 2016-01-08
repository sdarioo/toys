/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableInt;

public class UniqueNamesGenerator 
{
    private final Map<String, MutableInt> _names = new HashMap<String, MutableInt>();
    
    public UniqueNamesGenerator()
    {
    }
    
    public UniqueNamesGenerator(String... usedNames)
    {
        for (String name : usedNames) {
            generateUniqueName(name);
        }
    }
    
    public String generateUniqueName(String name)
    {
        MutableInt count = _names.get(name);
        if (count == null) {
            count = new MutableInt(0);
            _names.put(name, count);
            return name;
        }
        count.increment();
        return name + count.intValue();
    }
    
}
