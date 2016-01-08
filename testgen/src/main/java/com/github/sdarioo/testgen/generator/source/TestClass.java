/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.source;

import java.util.*;

import org.apache.commons.lang3.ClassUtils;

import com.github.sdarioo.testgen.util.Formatter;

public class TestClass
{
    private final String _qName;
    private final String _signature;
    private final List<String> _imports;
    private final List<TestMethod> _methods;
    private final List<ResourceFile> _resources;
    
    public TestClass(String name, String signature, 
            Collection<String> imports, 
            Collection<TestMethod> methods, 
            Collection<ResourceFile> resources)
    {
        _qName = name;
        _signature = signature;
        _imports = new ArrayList<String>(imports);
        _methods = new ArrayList<TestMethod>(methods);
        _resources = new ArrayList<ResourceFile>(resources);
    }
    
    public String getPackage()
    {
        return ClassUtils.getPackageCanonicalName(_qName);
    }
    
    public String getFileName()
    {
        String name = ClassUtils.getShortCanonicalName(_qName);
        return name + ".java"; //$NON-NLS-1$
    }
    
    @SuppressWarnings("nls")
    public String toSourceCode() 
    {
        StringBuilder result = new StringBuilder();
        
        String pkg = getPackage();
        if (pkg != null && pkg.length() > 0) {
            result.append("package ").append(pkg).append(";\n\n");
        }
        
        for (String imprt : _imports) {
            result.append("import " + imprt).append(';').append('\n');
        }
        result.append('\n').append('\n');
        result.append(_signature).append('\n');
        result.append('{').append('\n');
        
        for (TestMethod method : _methods) {
            String sourceCode = method.toSourceCode();
            sourceCode = Formatter.indentLines(sourceCode, "    ");
            result.append(sourceCode).append('\n').append('\n');
        }
        result.append('}').append('\n');
        return result.toString();
    }
    
    public List<ResourceFile> getResources() 
    {
        return Collections.unmodifiableList(_resources);
    }

}
