package com.github.sdarioo.testgen.instrument;

import java.lang.instrument.Instrumentation;

import com.github.sdarioo.testgen.generator.Generator;
import com.github.sdarioo.testgen.logging.Logger;

public class Agent 
{
    public static void premain(String args, Instrumentation inst) 
    {
        Logger.info("Agent started.");
        
        AgentArgs parsedArgs = parseArgs(args);
        if (parsedArgs == null) {
            logUsage();
            return;
        }
        TestGenTransformer transformer = new TestGenTransformer(parsedArgs.clazz, parsedArgs.method);
        inst.addTransformer(transformer);
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                Logger.info("Agent ShutdownHook begin.");
                Generator.generateTests();
                Logger.shutdown();
            }
        }));
    }
    
    private static AgentArgs parseArgs(String args)
    {
        if ((args == null) || (args.length() == 0)) {
            return null;
        }
        int index = args.lastIndexOf('.');
        if (index > 0) {
            return new AgentArgs(args.substring(0, index), args.substring(index + 1));
        }
        return new AgentArgs(args);
    }
    
    private static void logUsage()
    {
        Logger.warn("Missing agent parameters. Usage: -javaagent:<agent_jar>=<class>.<method>");
    }
    
    private static class AgentArgs
    {
        final String clazz;
        final String method;
        
        AgentArgs(String clazz)
        {
            this(clazz, TestGenClassAdapter.ALL_METHODS);
        }
        
        AgentArgs(String clazz, String method)
        {
            this.clazz = clazz;
            this.method = method;
        }
    }
}
