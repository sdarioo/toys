/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator;

import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.*;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

import com.github.sdarioo.testgen.generator.impl.UniqueNamesGenerator;
import com.github.sdarioo.testgen.generator.source.ResourceFile;
import com.github.sdarioo.testgen.generator.source.TestClass;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.instrument.InstrumentUtil;
import com.github.sdarioo.testgen.util.FileUtil;

public class TestSuiteBuilder
    implements IUniqueNamesProvider
{
    private final UniqueNamesGenerator _methodNames = new UniqueNamesGenerator();
    private final UniqueNamesGenerator _fileNames = new UniqueNamesGenerator();
    
    private final boolean _useFullTypeNames;
    
    private String _qName;
    private String _signature;
    
    private final Set<String> _imports = new HashSet<String>();
    private final List<TestMethod> _testCases = new ArrayList<TestMethod>();
    private final Map<String, TestMethod> _helperMethods = new HashMap<String, TestMethod>();
    private final List<ResourceFile> _resources = new ArrayList<ResourceFile>();
    
    private final Map<Class<?>, String> _templatesCache = new HashMap<Class<?>, String>();
    
    private int _helperMethodOrder = 1000000;
    
    public TestSuiteBuilder()
    {
        this(false);
    }
    
    public TestSuiteBuilder(boolean useFullTypeNames)
    {
        _useFullTypeNames = useFullTypeNames;
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
    
    public ResourceFile addResource(byte[] bytes, String nameSuffix)
    {
        // Lookup existing resource with the same binary content
        for (ResourceFile res : _resources) {
            if (Objects.equals(res.getBinaryContent(), bytes)) {
                return res;
            }
        }
        String fileName = toResourceName(nameSuffix);
        String uniqueName = newUniqueFileName(fileName);
        
        ResourceFile resource = new ResourceFile("res/" + uniqueName, bytes); //$NON-NLS-1$
        _resources.add(resource);
        return resource;
    }
    
    public ResourceFile addResource(String content, String nameSuffix)
    {
        // Lookup existing resource with the same text content
        for (ResourceFile res : _resources) {
            if (Objects.equals(res.getContent(), content)) {
                return res;
            }
        }
        String fileName = toResourceName(nameSuffix);
        String uniqueName = newUniqueFileName(fileName);
        
        ResourceFile resource = new ResourceFile("res/" + uniqueName, content); //$NON-NLS-1$
        _resources.add(resource);
        return resource;
    }
    
    public String getGenericTypeName(java.lang.reflect.Type type)
    {
        if (type == null) {
            return null;
        }
        if (type instanceof Class<?>) {
            return getTypeName((Class<?>)type);
        }
       
        if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)type;
            java.lang.reflect.Type rawType = ptype.getRawType();
            String rawTypeName = getGenericTypeName(rawType);
            if (rawTypeName == null) {
                return null;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append(rawTypeName);
            
            java.lang.reflect.Type[] args = ptype.getActualTypeArguments();
            if ((args != null) && (args.length > 0)) {
                sb.append('<');
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) {
                        sb.append(", "); //$NON-NLS-1$
                    }
                    String argName = getGenericTypeName(args[i]);
                    if (argName == null) {
                        return null;
                    }
                    sb.append(argName);
                }
                sb.append('>');
            }
            return sb.toString();    
        }
        return null;
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
        if (_useFullTypeNames) {
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
                String outClassName = ClassUtils.getShortCanonicalName(componentType);
                if (outClassName.indexOf('.') > 0) {
                    outClassName = outClassName.substring(0, outClassName.indexOf('.'));
                }
                addImport(pkg + '.' + outClassName);
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

    public Map<Class<?>, String> getTemplatesCache()
    {
        return _templatesCache;
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
