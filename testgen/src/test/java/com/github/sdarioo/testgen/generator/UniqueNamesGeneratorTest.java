/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.impl.UniqueNamesGenerator;

public class UniqueNamesGeneratorTest
{
    @SuppressWarnings("nls")
    @Test
    public void testGetUniqueName()
    {
        UniqueNamesGenerator gen = new UniqueNamesGenerator();
        assertEquals("name", gen.generateUniqueName("name"));
        assertEquals("name1", gen.generateUniqueName("name"));
        assertEquals("name2", gen.generateUniqueName("name"));
        
        assertEquals("other", gen.generateUniqueName("other"));
        assertEquals("other1", gen.generateUniqueName("other"));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testExistingUniqueName()
    {
        UniqueNamesGenerator gen = new UniqueNamesGenerator("name", "other");
        
        assertEquals("name1", gen.generateUniqueName("name"));
        assertEquals("name2", gen.generateUniqueName("name"));
        
        assertEquals("other1", gen.generateUniqueName("other"));
        
        assertEquals("new", gen.generateUniqueName("new"));
    }
}
