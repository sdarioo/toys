/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params;

import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;

import com.github.sdarioo.testgen.Configuration;
import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.ResourceFile;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.IParameter;

public class StringParam
    implements IParameter
{
    private final String _value;
    
    /**
     * @param value string value, never null
     * @pre value != null
     */
    public StringParam(String value)
    {
        _value = value;
    }
    
    @Override
    public boolean isSupported(Collection<String> errors) 
    {
        return true;
    }
    
    @Override
    public String toSouceCode(TestSuiteBuilder builder) 
    {
        int maxLength = Configuration.getDefault().getMaxStringLength();
        if (_value.length() <= maxLength) {
            return '\"' + StringEscapeUtils.escapeJava(_value) + '\"';
        }
        // Long strings will be placed in external resource files
        builder.addImport("java.io.*");
        ResourceFile resFile = builder.addResource(_value, "str");
        TestMethod resMethod = builder.addHelperMethod(RES_TEMPLATE, "res");
        return resMethod.getName() + "(\"" + resFile.getFileName() + "\")";
    }
    
    @Override
    public String toString() 
    {
        return _value;
    }
    
    @Override
    public boolean equals(Object obj) 
    {
        if (!(obj instanceof StringParam)) {
            return false;
        }
        StringParam other = (StringParam)obj;
        return (_value != null) ? _value.equals(other._value) : (other._value == null);
    }
    
    @Override
    public int hashCode() 
    {
        return (_value != null) ? _value.hashCode() : 0;
    }
    
    static int getLinesCount(String str)
    {
        int count = 1;
        
        int lidx = 0;
        int ridx = str.indexOf('\n');
        while (ridx >= lidx) {
            count++;
            lidx = ridx + 1;
            ridx = str.indexOf('\n', lidx);
        }
        return count;
    }
    
    private static final String RES_TEMPLATE =
            "private String {0}(String res) '{'\n" +
            "    StringBuilder sb = new StringBuilder();\n" +
            "    try '{'\n" + 
            "        InputStream is = getClass().getResourceAsStream(res);\n" +
            "        BufferedReader reader = new BufferedReader(new InputStreamReader(is, \"UTF-8\"));\n" +
            "        char[] cbuf = new char[1024];\n" +
            "        int c = 0;\n" +
            "        while ((c = reader.read(cbuf)) > 0) '{'\n" +
            "            sb.append(cbuf, 0, c);\n" +
            "        '}'\n" +
            "        is.close();\n" +
            "    '}' catch (IOException e) '{' Assert.fail(e.getMessage()); '}'\n" +
            "    return sb.toString();\n" +
            "'}'\n";
    
}
