/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.sdarioo.testgen.recorder.Call.MethodRef;

public class RecorderTest 
{
 
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
    

}
