/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.text.StrTokenizer;
import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.impl.JUnitParamsGenerator;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.params.ParamsFactory;

public class JUnitParamsGeneratorTest
{
    @SuppressWarnings("nls")
    @Test
    public void testGenerateTestMethod()
    {
        Method method = getMethod("sayHello");
        assertNotNull(method);
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        
        TestMethod source = gen.generateTestCase(method, new TestSuiteBuilder());
        assertNotNull(source.toSourceCode());
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGenerateParmasProviderMethod()
    {
        Method method = getMethod("sayHello");
        assertNotNull(method);
        
        List<Call> calls = new ArrayList<Call>();
        calls.add(new Call(method, ParamsFactory.newValue("ret1"), ParamsFactory.newValue("name1"), ParamsFactory.newValue(1)));
        calls.add(new Call(method, ParamsFactory.newValue(null), ParamsFactory.newValue(null), ParamsFactory.newValue(2)));
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();

        String text = gen.generateParamsProvider(method, calls, "testSayHello", new TestSuiteBuilder()).toSourceCode();
        assertNotNull(text);

        String[] lines = new StrTokenizer(text, '\n').getTokenArray();
        assertEquals(6, lines.length);
        assertEquals("private Object[] testSayHello_Parameters() {", lines[0].trim());
        assertEquals("return new Object[] {", lines[1].trim());
        assertEquals("new Object[]{ \"name1\", 1, \"ret1\" },", lines[2].trim());
        assertEquals("new Object[]{ null, 2, null }", lines[3].trim());
        assertEquals("};", lines[4].trim());
        assertEquals("}", lines[5].trim());
    }

    @SuppressWarnings("nls")
    @Test
    public void testGenerateTestSuite()
    {
        Method method = getMethod("sayHello");
        assertNotNull(method);
        
        List<Call> calls = new ArrayList<Call>();
        calls.add(new Call(method, ParamsFactory.newValue("ret1"), ParamsFactory.newValue("name1"), ParamsFactory.newValue(1)));
        calls.add(new Call(method, ParamsFactory.newValue(null), ParamsFactory.newValue(null), ParamsFactory.newValue(1)));
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        
        String text = gen.generate(method.getDeclaringClass(), calls).toSourceCode();
        assertNotNull(text);
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGenerateTestSuiteWithProperties()
    {
        Method method = getMethod("methodWithProperties");
        assertNotNull(method);
        
        Properties p1 = new Properties();
        Properties p2 = new Properties();
        p1.setProperty("key1", "value1");
        p2.setProperty("key2", "value2");
        
        List<Call> calls = new ArrayList<Call>();
        calls.add(new Call(method, ParamsFactory.newValue(0), ParamsFactory.newValue(p1)));
        calls.add(new Call(method, ParamsFactory.newValue(0), ParamsFactory.newValue(p2)));
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        
        String text = gen.generate(method.getDeclaringClass(), calls).toSourceCode();
        
        System.out.println(text);
    }
    
    public String sayHello(String name, int index)
    {
        return null;
    }
    
    public int methodWithProperties(Properties p)
    {
        return 0;
    }
    
    private Method getMethod(String name)
    {
        Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            if (name.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

}
