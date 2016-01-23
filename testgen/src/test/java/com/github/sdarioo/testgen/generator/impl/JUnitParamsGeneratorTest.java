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
        Call c1 = Call.newCall(method, "name1", 1);
        Call c2 = Call.newCall(method, null, 1);
        c1.endWithResult("ret1");
        c2.endWithResult(null);
        calls.add(c1);
        calls.add(c2);
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        String src = gen.generate(method.getDeclaringClass(), calls).toSourceCode();
        Set<String> set = toLines(src);
        
        assertTrue(set.contains("public void testSayHello(String arg0, int arg1, String expected) throws Exception {"));
        assertTrue(set.contains("String result = JUnitParamsGeneratorTest.sayHello(arg0, arg1);"));
        assertTrue(set.contains("Assert.assertEquals(expected, result);"));
        
        assertTrue(set.contains("public void testSayHello(String arg0, int arg1, String expected) throws Exception {"));
        assertTrue(set.contains("String result = JUnitParamsGeneratorTest.sayHello(arg0, arg1);"));
        assertTrue(set.contains("new Object[]{ \"name1\", 1, \"ret1\" },"));
        assertTrue(set.contains("new Object[]{ null, 1, null },"));
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
        Set<String> set = toLines(src);
        
        assertTrue(set.contains("@Test(expected=IllegalArgumentException.class)"));
        assertTrue(set.contains("JUnitParamsGeneratorTest obj = new JUnitParamsGeneratorTest();"));
        assertTrue(set.contains("obj.methodWithProperties(null);"));
    }
    
    @Test
    public void testNonDefaultConstructor() throws Exception
    {
        Method method = Inner.class.getMethod("foo", Integer.TYPE);
        Call call = Call.newCall(method, new Inner("", true, 100L, E.value), new Object[]{Integer.valueOf(1)});
        call.endWithResult(Integer.valueOf(1));
        
        JUnitParamsGenerator gen = new JUnitParamsGenerator();
        String src = gen.generate(method.getDeclaringClass(), Collections.singletonList(call)).toSourceCode();
        Set<String> set = toLines(src);
        
        assertTrue(set.contains("// WARNING - constructing JUnitParamsGeneratorTest.Inner with default parameters;"));
        assertTrue(set.contains("JUnitParamsGeneratorTest.Inner obj = new JUnitParamsGeneratorTest.Inner(null, false, 0L, JUnitParamsGeneratorTest.E.value);"));
        assertTrue(set.contains("int result = obj.foo(arg0);"));
    }
    
    public static String sayHello(String name, int index)
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
    
    public static class Inner
    {
        public Inner(String s, boolean b, long l, E e) {}
        public int foo(int a) { return 0; }
    }
    
    public static enum E { value; };
}
