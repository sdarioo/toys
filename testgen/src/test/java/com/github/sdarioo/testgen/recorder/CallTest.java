/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.github.sdarioo.testgen.recorder.Call.MethodRef;
import com.github.sdarioo.testgen.recorder.params.ParamsFactory;

public class CallTest 
{
    @Test
    public void testMethodRef()
    {
        Call call = Call.newCall(new MethodRef() {});
        assertNotNull(call);
    }
    
    @Test
    public void testEqualsMethod()
    {
        Call call1 = Call.newCall(new MethodRef() {});
        Call call2 = Call.newCall(new MethodRef() {});
        assertEquals(call1.getMethod(), call2.getMethod());
        
        Class<?> retType = call1.getMethod().getReturnType();
        assertEquals(Void.TYPE, retType);
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testEquals()
    {
        Call call1 = Call.newCall(new MethodRef() {});
        Call call2 = Call.newCall(new MethodRef() {});
        
        // Empty
        assertEquals(call1, call2);
        assertEquals(call1.hashCode(), call2.hashCode());
        
        // With args
        call1 = Call.newCall(new MethodRef() {}, 1);
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2 = Call.newCall(new MethodRef() {}, 1);
        assertEquals(call1, call2);
        assertEquals(call1.hashCode(), call2.hashCode());
        
        call1 = Call.newCall(new MethodRef() {}, 1, "test");
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2 = Call.newCall(new MethodRef() {}, 1, "test");
        assertEquals(call1, call2);
        assertEquals(call1.hashCode(), call2.hashCode());
        
        // With Result
        call1.endWithResult(1);
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2.endWithResult(2);
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2.endWithResult(1);
        assertEquals(call1, call2);
        assertEquals(call1.hashCode(), call2.hashCode());
        
        
        // With Exception
        call1.endWithException(new NullPointerException("null"));
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2.endWithException(new NullPointerException("other null"));
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2.endWithException(new IllegalArgumentException("null"));
        assertNotEquals(call1, call2);
        assertNotEquals(call1.hashCode(), call2.hashCode());
        
        call2.endWithException(new NullPointerException("null"));
        assertEquals(call1, call2);
        assertEquals(call1.hashCode(), call2.hashCode());
    }
}
