/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.asmtest;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import com.github.sdarioo.asmtest.transform.DummyTransformer;

public class Agent 
{
    public static void premain(String args, Instrumentation inst) 
    {
        ClassFileTransformer transformer = new DummyTransformer();
        inst.addTransformer(transformer);
    }
}
