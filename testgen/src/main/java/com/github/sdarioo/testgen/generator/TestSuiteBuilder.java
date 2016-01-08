/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.generator.source.ResourceFile;
import com.github.sdarioo.testgen.generator.impl.UniqueNamesGenerator;
import com.github.sdarioo.testgen.generator.source.TestClass;
import com.github.sdarioo.testgen.util.FileUtil;

public class TestSuiteBuilder
    implements IUniqueNamesProvider
{
    private final UniqueNamesGenerator _methodNames = new UniqueNamesGenerator();
    private final UniqueNamesGenerator _fileNames = new UniqueNamesGenerator();
    
    private String _qName;
    private String _signature;
    
    private final Set<String> _imports = new HashSet<String>();
    private final List<TestMethod> _testCases = new ArrayList<TestMethod>();
    private final Map<String, TestMethod> _helperMethods = new HashMap<String, TestMethod>();
    private final List<ResourceFile> _resources = new ArrayList<ResourceFile>();
    
    
    public TestSuiteBuilder()
    {
    }
    
    public TestClass buildTestClass()
    {
        List<TestMethod> methods = new ArrayList<TestMethod>();
        methods.addAll(_testCases);
        methods.addAll(_helperMethods.values());
        
        return new TestClass(_qName, _signature, _imports, methods, _resources);
    }
    
    public void addImport(String classOrPkg) 
    {
        _imports.add(classOrPkg);
    }
    
    public void setCanonicalName(String qName)
    {
        _qName = qName;
    }

    public void setSignature(String signature) 
    {
        _signature = signature;
    }
    
    public void addTestCase(TestMethod testCase)
    {
        _testCases.add(testCase);
    }
    
    public TestMethod addHelperMethod(String template, String methodName)
    {
        TestMethod method = _helperMethods.get(template);
        if (method == null) {
            String uniqueName = newUniqueMethodName(methodName);
            String sourceCode = MessageFormat.format(template, uniqueName);
            method = new TestMethod(uniqueName, sourceCode);
            _helperMethods.put(template, method);    
        }
        return method;
    }
    
    public ResourceFile addResource(String content, String fileName)
    {
        String uniqueName = newUniqueFileName(fileName);
        ResourceFile resource = new ResourceFile(uniqueName, content);
        _resources.add(resource);
        return resource;
    }

    /**
     * @see com.github.sdarioo.testgen.generator.IUniqueNamesProvider#newUniqueMethodName(java.lang.String)
     */
    @Override
    public String newUniqueMethodName(String methodName) 
    {
        return _methodNames.generateUniqueName(methodName);
    }
    
    /**
     * @see com.github.sdarioo.testgen.generator.IUniqueNamesProvider#newUniqueFileName(java.lang.String)
     */
    @Override
    public String newUniqueFileName(String fileName) 
    {
        Pair<String, String> pair = FileUtil.getNameWithExtension(fileName);
        String baseName = pair.getLeft();
        String uniqueName = _fileNames.generateUniqueName(baseName);
        if (pair.getRight() != null) {
            return uniqueName + '.' + pair.getRight();
        }
        return uniqueName;
    }
}
