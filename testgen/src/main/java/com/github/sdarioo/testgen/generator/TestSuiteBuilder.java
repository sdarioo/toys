/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.lang3.ClassUtils;
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
    
    private final boolean _bFullTypeNames;
    
    private String _qName;
    private String _signature;
    
    private final Set<String> _imports = new HashSet<String>();
    private final List<TestMethod> _testCases = new ArrayList<TestMethod>();
    private final Map<String, TestMethod> _helperMethods = new HashMap<String, TestMethod>();
    private final List<ResourceFile> _resources = new ArrayList<ResourceFile>();
    
    
    public TestSuiteBuilder()
    {
        this(false);
    }
    
    public TestSuiteBuilder(boolean bFullTypeNames)
    {
        _bFullTypeNames = bFullTypeNames;
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
     * Returns type name used by code generators. If using short type names than
     * apropriate import will be added to this builder.
     * @param type class
     * @param builder test class builder
     * @return type name 
     */
    public String getTypeName(Class<?> type)
    {
        String name = fullTypeName(type);
        if (_bFullTypeNames) {
            return name;
        }
        name = ClassUtils.getShortCanonicalName(type);
        
        if (type.isArray()) {
            type = getArrayType(type);
        }
        if (!type.isPrimitive()) {
            addImport(ClassUtils.getPackageCanonicalName(type) + '.' + ClassUtils.getShortCanonicalName(type));
        }
        return name;
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
    
    /**
     * Returns full type name for given class. 
     * This implementation is based on {@link Class#getTypeName()}
     * @param type class
     * @return full type name
     */
    private static String fullTypeName(Class<?> type) 
    {
        if (type.isArray()) {
            try {
                Class<?> cl = type;
                int dimensions = 0;
                while (cl.isArray()) {
                    dimensions++;
                    cl = cl.getComponentType();
                }
                StringBuffer sb = new StringBuffer();
                sb.append(cl.getName());
                for (int i = 0; i < dimensions; i++) {
                    sb.append("[]"); //$NON-NLS-1$
                }
                return sb.toString();
            } catch (Throwable e) { /*FALLTHRU*/ }
        }
        return type.getName();
    }

    private static Class<?> getArrayType(Class<?> type)
    {
        Class<?> cl = type;
        while (cl.isArray()) {
            cl = cl.getComponentType();
        }
        return cl;
    }
}
