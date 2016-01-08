/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.source;

import java.util.*;

import com.github.sdarioo.testgen.util.Formatter;

public class TestClass
{
    private final String _name;
    private final String _signature;
    private final List<String> _imports;
    private final List<TestMethod> _methods;
    private final List<ResourceFile> _resources;
    
    public TestClass(String name, String signature, 
            Collection<String> imports, 
            Collection<TestMethod> methods, 
            Collection<ResourceFile> resources)
    {
        _name = name;
        _signature = signature;
        _imports = new ArrayList<String>(imports);
        _methods = new ArrayList<TestMethod>(methods);
        _resources = new ArrayList<ResourceFile>(resources);
    }
    
    public String getName()
    {
        return _name;
    }
    
    public String getFileName()
    {
        return getName() + ".java"; //$NON-NLS-1$
    }
    
    public String toSourceCode() 
    {
        StringBuilder result = new StringBuilder();
        for (String imprt : _imports) {
            result.append("import " + imprt).append(';').append('\n'); //$NON-NLS-1$
        }
        result.append('\n').append('\n');
        result.append(_signature).append('\n');
        result.append('{').append('\n');
        
        for (TestMethod method : _methods) {
            String sourceCode = method.toSourceCode();
            sourceCode = Formatter.indentLines(sourceCode, "    "); //$NON-NLS-1$
            result.append(sourceCode).append('\n');
        }
        result.append('}').append('\n');
        return result.toString();
    }
    
    public List<ResourceFile> getResources() 
    {
        return Collections.unmodifiableList(_resources);
    }

}
