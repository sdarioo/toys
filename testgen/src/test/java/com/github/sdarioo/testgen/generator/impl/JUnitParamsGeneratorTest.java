/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.impl;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.lang3.text.StrTokenizer;
import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.Call;

public class JUnitParamsGeneratorTest
{
    @SuppressWarnings("nls")
    @Test
    public void testGenerateTestMethod()
    {
        Method method = getMethod("sayHello");
        assertNotNull(method);
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        
        TestMethod source = gen.generateTestCase(this.getClass(), method, new TestSuiteBuilder());
        assertNotNull(source.toSourceCode());
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testGenerateParmasProviderMethod()
    {
        Method method = getMethod("sayHello");
        assertNotNull(method);
        
        List<Call> calls = new ArrayList<Call>();
        calls.add(Call.newCall(method, "name1", 1));
        calls.add(Call.newCall(method, null, 2));
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();

        String text = gen.generateParamsProvider(method, calls, "testSayHello", new TestSuiteBuilder()).toSourceCode();
        assertNotNull(text);

        String[] lines = new StrTokenizer(text, '\n').getTokenArray();
        assertEquals(6, lines.length);
        assertEquals("private Object[] testSayHello_Parameters() {", lines[0].trim());
        assertEquals("return new Object[] {", lines[1].trim());
        assertEquals("new Object[]{ \"name1\", 1, null },", lines[2].trim());
        assertEquals("new Object[]{ null, 2, null },", lines[3].trim());
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
        calls.add(Call.newCall(method, "ret1", "name1", 1));
        calls.add(Call.newCall(method, null, null, 1));
        
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
        calls.add(Call.newCall(method, 0, p1));
        calls.add(Call.newCall(method, 0, p2));
        
        calls.get(0).endWithResult(null);
        calls.get(1).endWithResult(null);
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        
        String src = gen.generate(method.getDeclaringClass(), calls).toSourceCode();
        
        Set<String> set = toLines(src);
        
        assertTrue(set.contains("@Parameters(method = \"testMethodWithProperties_Parameters\")"));
        assertTrue(set.contains("private Object[] testMethodWithProperties_Parameters() {"));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testStaticCallException()
    {
        Method method = getMethod("staticMethodWithProperties");
        Call call = Call.newCall(method, (Object)null);
        call.endWithException(new IllegalArgumentException());
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        
        String src = gen.generate(method.getDeclaringClass(), Collections.singletonList(call)).toSourceCode();
        
        System.out.println(src);
        
        Set<String> set = toLines(src);
        
        assertTrue(set.contains("@Test(expected=IllegalArgumentException.class)"));
        assertTrue(set.contains("JUnitParamsGeneratorTest.staticMethodWithProperties(null);"));
    }
    
    @SuppressWarnings("nls")
    @Test
    public void testCallException()
    {
        Method method = getMethod("methodWithProperties");
        Call call = Call.newCall(method, (Object)null);
        call.endWithException(new IllegalArgumentException());
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        
        String src = gen.generate(method.getDeclaringClass(), Collections.singletonList(call)).toSourceCode();
        
        System.out.println(src);
        
        Set<String> set = toLines(src);
        
        assertTrue(set.contains("@Test(expected=IllegalArgumentException.class)"));
        assertTrue(set.contains("JUnitParamsGeneratorTest obj = new JUnitParamsGeneratorTest();"));
        assertTrue(set.contains("obj.methodWithProperties(null);"));
    }
    
    public String sayHello(String name, int index)
    {
        return null;
    }
    
    public int methodWithProperties(Properties p)
    {
        return 0;
    }
    
    public static int staticMethodWithProperties(Properties p)
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

    private static Set<String> toLines(String src)
    {
        String[] lines = src.split("\\n");
        Set<String> set = new HashSet<String>();
        for (String line : lines) {
            set.add(line.trim());
        }
        return set;
    }
}
