/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.sdarioo.testgen.generator.impl.JUnitParamsGenerator;
import com.github.sdarioo.testgen.generator.source.ResourceFile;
import com.github.sdarioo.testgen.generator.source.TestClass;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.Recorder;
import com.github.sdarioo.testgen.util.FileUtil;

public class Generator
{
    private static AtomicBoolean _shutdownHookRegistered = new AtomicBoolean(false);
    
    
    public static void registerShutdownHook()
    {
        if (_shutdownHookRegistered.compareAndSet(false, true)) {
           Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    Recorder recorder = Recorder.getDefault();
                    generateTestsSafe(recorder);
                }
            }));
        }
    }
    
    public static void generateTests(Recorder recorder)
        throws IOException
    {
        Collection<Class<?>> classes = recorder.getRecordedClasses();
        for (Class<?> clazz : classes) {
            ITestSuiteGenerator generator = getTestSuiteGenerator(clazz);
            List<Call> calls = recorder.getCalls(clazz);
            TestClass testSuite = generator.generate(clazz, calls);
            File destDir = getTestDestination(clazz);
            write(testSuite, destDir);
        }
    }
    
    private static void generateTestsSafe(Recorder recorder)
    {
        try {
            generateTests(recorder);
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        }
    }
    
    private static ITestSuiteGenerator getTestSuiteGenerator(Class<?> testedClass)
    {
        return new JUnitParamsGenerator();
    }
    
    private static File getTestDestination(Class<?> testedClass)
    {
        // TODO
        return new File("D:\\temp\\testgen");
    }
    
    private static void write(TestClass testSuite, File destDir)
        throws IOException
    {
        String content = testSuite.toSourceCode();
        File file = new File(destDir, testSuite.getFileName());
        FileUtil.write(file, content);
        
        for (ResourceFile res : testSuite.getResources()) {
            content = res.getContent();
            file = new File(destDir, res.getFileName());
            FileUtil.write(file, content);
        }
    }
}
