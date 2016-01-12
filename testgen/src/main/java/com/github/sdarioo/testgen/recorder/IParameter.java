/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.util.Collection;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;

/**
 * Base abstraction for method call parameter value
 */
public interface IParameter 
{
    /**
     * @param errors collection for reason why param cannot be represented in source code
     * @return whether given parameter can be represented in source code
     */
    boolean isValid(Collection<String> errors);
    
    /**
     * @return source code text used by source code generator. May be null if
     * parameter is not valid {{@link #isValid(StringBuilder)} 
     */
    String toSouceCode(TestSuiteBuilder builder);
    
    @Override
    boolean equals(Object obj);
    
    @Override
    int hashCode();
    
    
    /** Special value that represents result of void return methods */
    public static final IParameter VOID = new IParameter() {
        public boolean isValid(Collection<String> errors) { return true; };
        public String toSouceCode(TestSuiteBuilder builder) { return null; };
    };
    
    /** Special value that represents null object */
    public static final IParameter NULL = new IParameter() { 
        public boolean isValid(Collection<String> errors) { return true; };
        public String toSouceCode(TestSuiteBuilder builder) { return "null"; }; //$NON-NLS-1$
    };

}
