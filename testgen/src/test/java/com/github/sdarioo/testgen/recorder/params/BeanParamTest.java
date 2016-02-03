package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
import com.github.sdarioo.testgen.generator.source.TestMethod;
import com.github.sdarioo.testgen.recorder.params.beans.BeanFactory;

public class BeanParamTest 
{
    @Test
    public void testEquals()
    {
        BeanParam p1 = new BeanParam(new Bean1(), BeanFactory.getInstance().getBean(Bean1.class));
        BeanParam p2 = new BeanParam(new Bean1(), BeanFactory.getInstance().getBean(Bean1.class));
        BeanParam p3 = new BeanParam(new Bean2(new Bean1()), BeanFactory.getInstance().getBean(Bean2.class));
        BeanParam p4 = new BeanParam(new Bean2(null), BeanFactory.getInstance().getBean(Bean2.class));
        
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p4, p3);
    }
    
    @Test
    public void toSourceCode()
    {
        BeanParam p1 = new BeanParam(new Bean1(), BeanFactory.getInstance().getBean(Bean1.class));
        BeanParam p2 = new BeanParam(new Bean2(new Bean1()), BeanFactory.getInstance().getBean(Bean2.class));
        BeanParam p3 = new BeanParam(new Bean3(), BeanFactory.getInstance().getBean(Bean3.class));
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("newBean1(0, 0)", p1.toSouceCode(builder));
        assertEquals("newBean2(newBean1(0, 0))", p2.toSouceCode(builder));
        assertEquals("newBean3(0)", p3.toSouceCode(builder));
    }
    
    @Test
    public void testSimpleBeans()
    {
        BeanParam p1 = new BeanParam(new Bean1(), BeanFactory.getInstance().getBean(Bean1.class));
        BeanParam p2 = new BeanParam(new Bean2(null), BeanFactory.getInstance().getBean(Bean2.class));
        BeanParam p3 = new BeanParam(new Bean3(), BeanFactory.getInstance().getBean(Bean3.class));
        
        testBeanParam(p1, "newBean1(0, 0)", "private static BeanParamTest.Bean1 newBean1(int x, int y) {");
        testBeanParam(p2, "newBean2(null)", "private static BeanParamTest.Bean2 newBean2(BeanParamTest.Bean1 x) {");
        testBeanParam(p3, "newBean3(0)", "private static BeanParamTest.Bean3 newBean3(int x) {");
    }
    
    
    @Test
    public void testRawBean() throws Exception
    {
        BeanParam p = new BeanParam(new Pair<Integer>(1,2), BeanFactory.getInstance().getBean(Pair.class));
        testBeanParam(p, "newPair(1, 2)", "private static BeanParamTest.Pair newPair(Object x, Object y) {");
    }
    
    @Test
    public void testGenericBean() throws Exception
    {
        Method m = getClass().getMethod("foo1", Pair.class);
        
        BeanParam p = new BeanParam(new Pair<Integer>(1,2), 
                BeanFactory.getInstance().getBean(Pair.class),
                m.getGenericParameterTypes()[0]);
        
        testBeanParam(p, "newPair(1, 2)", "private static BeanParamTest.Pair<Integer> newPair(Integer x, Integer y) {");
    }
    
    @Test
    public void testHelperMethods() throws Exception
    {
        Method m1 = getClass().getMethod("foo1", Pair.class);
        Method m2 = getClass().getMethod("foo2", Pair.class);
        
        BeanParam p1 = new BeanParam(new Pair<Integer>(1,2), 
                BeanFactory.getInstance().getBean(Pair.class),
                m1.getGenericParameterTypes()[0]);
        
        BeanParam p2 = new BeanParam(new Pair<String>("x", "y"), 
                BeanFactory.getInstance().getBean(Pair.class),
                m2.getGenericParameterTypes()[0]);
        
        assertFalse(p1.equals(p2));
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("newPair(1, 2)", p1.toSouceCode(builder));
        assertEquals("newPair2(\"x\", \"y\")", p2.toSouceCode(builder));
        
        assertEquals(2, builder.getHelperMethods().size());
    }
    
    
    private void testBeanParam(BeanParam p, String sourceCode, String... expectedLines)
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals(sourceCode, p.toSouceCode(builder));
        
        List<TestMethod> helperMethods = builder.getHelperMethods();
        assertEquals(1, helperMethods.size());
        
        String[] lines = helperMethods.get(0).toSourceCode().split("\\n");
        
        for (int i = 0; i < Math.min(expectedLines.length, lines.length); i++) {
            assertEquals(expectedLines[i], lines[0]);
        }
    }
    
    
 // DONT REMOVE - USED IN TEST
    public void foo1(Pair<Integer> pair) {}
    public void foo2(Pair<String> pair) {}
    
    public static class Bean1
    {
        int x, y;
    }
    
    public static class Bean2
    {
        private Bean1 x;
        Bean2(Bean1 x) { this.x = x; }
    }
    
    public static class Bean3
    {
        private int x;
        void setX(int x) { this.x = x;}
    }
    
    public static class Pair<T>
    {
        T x;
        T y;
        Pair(T x, T y) {
            this.x = x;
            this.y = y;
        }
    }
}
