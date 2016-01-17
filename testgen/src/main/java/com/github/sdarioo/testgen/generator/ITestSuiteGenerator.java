/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.util.List;

import com.github.sdarioo.testgen.generator.source.TestClass;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.IArgNamesProvider;

public interface ITestSuiteGenerator 
{
    TestClass generate(Class<?> testedClass, List<Call> recordedCalls);
    
    void setArgNamesProvider(IArgNamesProvider argNamesProvider);
}