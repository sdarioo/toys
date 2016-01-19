/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class App 
{
    public static void main(String[] args) 
    {
        for (int i = 0; i < 10; i++) {
            Properties p = new Properties();
            p.setProperty("key-"+i, "value-"+i);
            
            concat(p);
            add((short)i, (byte)i, Boolean.FALSE);
            
            Person person = new Person();
            person.setAge(10 + i);
            
            isAdult(new Person[]{person});
            isAdult(Arrays.asList(person));
        }

        trim("\nline\n");
        
        isMax(Long.MIN_VALUE);
        isMax(Long.MAX_VALUE);
        
        getMax(Long.MIN_VALUE);
        getMax(Long.MAX_VALUE);
    }
    
    public static boolean isMax(long m)
    {
        return m == Long.MAX_VALUE;
    }
    
    public static long getMax(long m)
    {
        return Long.MAX_VALUE;
    }
    
    public static String trim(String text)
    {
        return text.trim();
    }
    
    public static Double add(Short x, Byte y, Boolean b)
    {
        double result = x + y;
        return result;
    }
    
    public static String concat(Properties props)
    {
        StringBuilder sb = new StringBuilder();
        for (Object key : props.keySet()) {
            if (sb.length() > 0) {
                sb.append(';');
            }
            String sKey = (String)key;
            String sValue = props.getProperty(sKey);
            sb.append(sKey).append('=').append(sValue);
        }
        String ret = sb.toString();
        return ret;
    }
    
    public static boolean isAdult(Person[] p)
    {
        return true;
    }
    
    public static boolean isAdult(List<Person> p)
    {
        return true;
    }
    
    public static class Person
    {
        int age;
        String name;
        public void setAge(int age) {
            this.age = age;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
}
