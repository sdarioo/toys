/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.github.sdarioo.testgen.generator.impl.JUnitParamsGenerator;
import com.github.sdarioo.testgen.generator.source.ResourceFile;
import com.github.sdarioo.testgen.generator.source.TestClass;
import com.github.sdarioo.testgen.logging.Logger;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.Recorder;
import com.github.sdarioo.testgen.util.FileUtil;
import com.github.sdarioo.testgen.util.TestLocationUtil;

// ThreadSafe
public final class Generator
{
    private final Recorder _recorder;
    private volatile long _timestamp = 0L;
    
    private static final Generator DEFAULT = new Generator(Recorder.getDefault());

    private Generator(Recorder recorder) 
    {
        _recorder = recorder;
    }
    
    public static synchronized Generator getDefault()
    {
        return DEFAULT;
    }

    public void generateTests()
    {
        try {
            internalGenerateTests();
        } catch (Throwable e) {
            Logger.error("Error while generating tests.", e); //$NON-NLS-1$
        }
    }
    
    @SuppressWarnings("nls")
    private void internalGenerateTests()
        throws IOException
    {
        if (_timestamp == _recorder.getTimestamp()) {
            return;
        }
        _timestamp = _recorder.getTimestamp();
        
        Collection<Class<?>> classes = _recorder.getRecordedClasses();
        for (Class<?> clazz : classes) {
            File destDir = TestLocationUtil.getTestLocation(clazz);
            if (destDir == null) {
                Logger.error("Null test location for class: " + clazz.getName());
                continue;
            }
            List<Call> calls = _recorder.getCalls(clazz);
            ITestSuiteGenerator generator = getTestSuiteGenerator(clazz);
            generator.setArgNamesProvider(_recorder);
            TestClass testSuite = generator.generate(clazz, calls);
            if (write(testSuite, destDir)) {
                Logger.info("Generated test: " + destDir.getAbsolutePath() + File.separator + testSuite.getFileName());
            }
        }
    }
    
    private static ITestSuiteGenerator getTestSuiteGenerator(Class<?> testedClass)
    {
        return new JUnitParamsGenerator();
    }
    
    private synchronized static boolean write(TestClass testSuite, File destDir)
        throws IOException
    {
        if (!destDir.isDirectory() && !destDir.mkdirs()) {
            Logger.error("Cannot create test destination director: " + destDir.getAbsolutePath()); //$NON-NLS-1$
            return false;
        }
        
        String content = testSuite.toSourceCode();
        File file = new File(destDir, testSuite.getFileName());
        FileUtil.write(file, content);
        
        for (ResourceFile res : testSuite.getResources()) {
            content = res.getContent();
            file = new File(destDir, res.getFileName());
            file.getParentFile().mkdirs();
            FileUtil.write(file, content);
        }
        return true;
    }
  
}
