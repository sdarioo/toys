/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.instrument.InstrumentUtil;
import com.github.sdarioo.testgen.recorder.Call.MethodRef;
import com.github.sdarioo.testgen.recorder.params.ParamsFactory;
import com.github.sdarioo.testgen.recorder.params.UnknownParam;

public class RecorderTest 
{
    
    @Test
    public void testGet()
    {
        assertSame(Recorder.get("key"), Recorder.get("key"));
        assertNotSame(Recorder.get("key"), Recorder.get("other"));
    }
 
    @SuppressWarnings("nls")
    @Test
    public void testMethodHolderPerformance()
    {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Call call = Call.newCall(new MethodRef() {});
            assertNotNull(call);
        }
        long time = System.currentTimeMillis() - start;
        assertTrue("Actual time: " + time, time < 200);
    }
    
    @Test
    public void testGetValidCallsFirst()
    {
        Method m = InstrumentUtil.getMethod(RecorderTest.class, "testCall", "(Ljava/lang/Object;)I");
        assertNotNull(m);
        
        int max = Configuration.getDefault().getMaxCalls();
        Recorder r = Recorder.get("testGetCallsWithUnsupported");
        for (int i = 0; i < max; i++) {
            Call c = Call.newCall(m);
            c.args().add(ParamsFactory.newValue("param"+i));
            c.setResult(IParameter.VOID);
            r.record(c);
        }
        // This call will not be returned by getCalls because there is already max valid calls
        Call c = Call.newCall(m);
        c.args().add(new UnknownParam(Collections.class));
        c.setResult(IParameter.VOID);
        r.record(c);
        
        List<Call> calls = r.getCalls(m.getDeclaringClass());
        assertEquals(max, calls.size());
        assertFalse(calls.contains(c));
    }
    
    @Test
    public void testGetUnsupportedCalls()
    {
        Method m = InstrumentUtil.getMethod(RecorderTest.class, "testCall", "(Ljava/lang/Object;)I");
        assertNotNull(m);
        
        Recorder r = Recorder.get("testGetCallsWithUnsupported");
        
        Call c = Call.newCall(m);
        c.args().add(ParamsFactory.newValue("param"));
        c.setResult(IParameter.VOID);
        r.record(c);
        
        // This call will be returned because size of supported calls is less than max
        c = Call.newCall(m);
        c.args().add(new UnknownParam(Collections.class));
        c.setResult(IParameter.VOID);
        r.record(c);
        
        List<Call> calls = r.getCalls(m.getDeclaringClass());
        assertEquals(2, calls.size());
        assertTrue(calls.contains(c));
    }
    
    
    public int testCall(Object o)
    {
        return 0;
    }
    
}
