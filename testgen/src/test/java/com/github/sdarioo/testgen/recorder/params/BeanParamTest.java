package com.github.sdarioo.testgen.recorder.params;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.sdarioo.testgen.generator.TestSuiteBuilder;
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
}
