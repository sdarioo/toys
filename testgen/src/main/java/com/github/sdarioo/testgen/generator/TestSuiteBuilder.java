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
import org.objectweb.asm.Type;

import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.instrument.InstrumentUtil;
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
    
    private int _helperMethodOrder = 1000000;
    
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
            method = new TestMethod(uniqueName, sourceCode, _helperMethodOrder++);
            _helperMethods.put(template, method);    
        }
        return method;
    }
    
    public ResourceFile addResource(String content, String nameSuffix)
    {
        // Lookup existing resource with the same content
        for (ResourceFile res : _resources) {
            if (res.getContent().equals(content)) {
                return res;
            }
        }
        String fileName = toResourceName(nameSuffix);
        String uniqueName = newUniqueFileName(fileName);
        ResourceFile resource = new ResourceFile("res/" + uniqueName, content); //$NON-NLS-1$
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
        return getTypeName(type.getName());
    }
    
    public String getTypeName(String className)
    {
        if (_bFullTypeNames) {
            return className;
        }
        // Add required import
        String componentType = className;
        int index = className.lastIndexOf('[');
        if (index >= 0) {
            componentType =  getElementType(className);
        }
        if (componentType.lastIndexOf('.') > 0) {
            String pkg = ClassUtils.getPackageCanonicalName(componentType);
            if (pkg.length() > 0) {
                addImport(pkg + '.' + ClassUtils.getShortCanonicalName(componentType));
            }
        }
        // Create short name
        String shortName = ClassUtils.getShortCanonicalName(className);
        return shortName;
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

    // Method exposed for junit tests
    Set<String> getImports()
    {
        return Collections.unmodifiableSet(_imports);
    }
    

    private String toResourceName(String suffix)
    {
        if (_qName == null) {
            return suffix;
        }
        int index = _qName.lastIndexOf('.');
        return (index > 0) ? 
                _qName.substring(index + 1) + '_' + suffix : 
                _qName + '_' + suffix;
    }
    
    private static String getElementType(String arrayType)
    {
        Type type = Type.getType(arrayType);
        while (type.getSort() == Type.ARRAY) {
            type = type.getElementType();
        }
        return InstrumentUtil.isPrimitive(type) ? type.getClassName() : type.getInternalName().replace('/', '.');
    }

}