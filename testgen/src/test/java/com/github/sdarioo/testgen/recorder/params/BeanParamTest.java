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
        BeanParam p3 = new BeanParam(new Bean2(0), BeanFactory.getInstance().getBean(Bean2.class));
        BeanParam p4 = new BeanParam(new Bean2(1), BeanFactory.getInstance().getBean(Bean2.class));
        
        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertNotEquals(p4, p3);
    }
    
    @Test
    public void toSourceCode()
    {
        BeanParam p1 = new BeanParam(new Bean1(), BeanFactory.getInstance().getBean(Bean1.class));
        BeanParam p2 = new BeanParam(new Bean2(0), BeanFactory.getInstance().getBean(Bean2.class));
        BeanParam p3 = new BeanParam(new Bean3(), BeanFactory.getInstance().getBean(Bean3.class));
        
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals("newBean1(0, 0)", p1.toSouceCode(builder));
        assertEquals("newBean2(0)", p2.toSouceCode(builder));
        assertEquals("newBean3(0)", p3.toSouceCode(builder));
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
        Method m = getClass().getMethod("foo", Pair.class);
        
        BeanParam p = new BeanParam(new Pair<Integer>(1,2), 
                BeanFactory.getInstance().getBean(Pair.class),
                m.getGenericParameterTypes()[0]);
        
        testBeanParam(p, "newPair(1, 2)", "private static BeanParamTest.Pair<Integer> newPair(Integer x, Integer y) {");
    }
    
    
    private void testBeanParam(BeanParam p, String sourceCode, String expectedSignature)
    {
        TestSuiteBuilder builder = new TestSuiteBuilder();
        assertEquals(sourceCode, p.toSouceCode(builder));
        
        List<TestMethod> helperMethods = builder.getHelperMethods();
        assertEquals(1, helperMethods.size());
        
        String signature = helperMethods.get(0).toSourceCode().split("\\n")[0];
        assertEquals(expectedSignature, signature);
    }
    
    
 // DONT REMOVE - USED IN TEST
    public void foo(Pair<Integer> pair) {}
    
    public static class Bean1
    {
        int x, y;
    }
    
    public static class Bean2
    {
        private int x;
        Bean2(int x) { this.x = x; }
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
