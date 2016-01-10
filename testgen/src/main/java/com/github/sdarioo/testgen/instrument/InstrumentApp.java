package com.github.sdarioo.testgen.instrument;

import java.lang.instrument.Instrumentation;

public class InstrumentApp 
{
    public static void premain(String args, Instrumentation inst) 
    {
        // TODO - take from args
        String className = "App";
        String methodName = "*";
        
        TestGenTransformer transformer = new TestGenTransformer(className, methodName);
        inst.addTransformer(transformer);
    }
}
