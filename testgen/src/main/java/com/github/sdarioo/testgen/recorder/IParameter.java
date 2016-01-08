/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

/**
 * Base abstraction for method call parameter value
 */
public interface IParameter 
{
    /**
     * @return source code text used by source code generator 
     */
    String toSouceCode(TestSuiteBuilder builder);
    
    @Override
    boolean equals(Object obj);
    
    @Override
    int hashCode();
    
    
    /** Special value that represents result of void return methods */
    public static final IParameter VOID = new IParameter() { 
        public String toSouceCode(TestSuiteBuilder builder) { return null; };
    };
    
    /** Special value that represents null object */
    public static final IParameter NULL = new IParameter() { 
        public String toSouceCode(TestSuiteBuilder builder) { return "null"; }; //$NON-NLS-1$
    };
    
    /** Represents unsupported value types that cannot be marshaled/unmarshaled */
    public static final IParameter UNKNOWN = new IParameter() { 
        public String toSouceCode(TestSuiteBuilder builder) { return "null"; }; //$NON-NLS-1$
    };
}
