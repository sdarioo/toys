/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params.beans;

import static org.junit.Assert.*;

import org.junit.Test;

public class BeanFactoryTest 
{
    @Test
    public void testGetField() throws Exception
    {
        Simple s = new Simple();
        s.setAge(1);
        s.setName("name");
        
        java.lang.reflect.Field f = s.getClass().getDeclaredField("age");
        f.setAccessible(true);
        assertEquals(1, f.get(s));
        
    }
    
    @Test
    public void testEmptyBean()
    {
        BeanFactory f = BeanFactory.getInstance();
        Bean bean = f.getBean(Empty.class);
        assertNotNull(bean);
        assertTrue(bean.getConstructor().setters.isEmpty());
        assertTrue(bean.getGetters().isEmpty());
        assertTrue(bean.getGetters().isEmpty());
    }
    
    @Test
    public void testSimpleBean()
    {
        BeanFactory f = BeanFactory.getInstance();
        Bean bean = f.getBean(Simple.class);
        assertNotNull(bean);
        assertEquals(0, bean.getConstructor().setters.size());
        assertEquals(2, bean.getSetters().size());
        assertEquals(2, bean.getGetters().size());
    }
    
    @Test
    public void testNoSettersBean()
    {
        BeanFactory f = BeanFactory.getInstance();
        Bean bean = f.getBean(NoSetters.class);
        assertNotNull(bean);
        assertEquals(2, bean.getConstructor().setters.size());
        assertEquals(0, bean.getSetters().size());
        assertEquals(0, bean.getGetters().size());
    }
    
    @Test
    public void testMixedBean()
    {
        BeanFactory f = BeanFactory.getInstance();
        Bean bean = f.getBean(Mixed.class);
        assertNotNull(bean);
        assertEquals(1, bean.getConstructor().setters.size());
        assertEquals(1, bean.getSetters().size());
        assertEquals(2, bean.getGetters().size());
    }
    
    public static class Empty
    {
    }
    
    public static class Simple
    {
        private int age;
        private String name;
        
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class NoSetters
    {
        private int age;
        private String name;
        
        public NoSetters(int age, String name)
        {
            this.age = age;
            this.name = name;
        }
    }
    
    public static class Mixed
    {
        private int age;
        private String name;
        
        public Mixed(int age)        
        {
            this.age = age;
        }
        
        public int getAge() {
            return age;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
}
