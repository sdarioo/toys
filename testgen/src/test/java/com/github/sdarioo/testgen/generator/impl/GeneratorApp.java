/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GeneratorApp 
{
    public static void main(String[] args) 
    {
        Pair<String> p1 = new Pair<String>("a", "b");
        Pair<String> p2 = new Pair<String>("x", "y");
     
         
        List<Pair<String>> pairList = Arrays.asList(p1, p2);
        count(pairList);
        countGeneric(pairList);
        
        StringList stringList = new StringList(Arrays.asList("x", "y", "z"));
        concat(stringList);
        
        List<String> listOfString = Arrays.asList("c", "b", "a");
        sort(listOfString);
        
        
        System.out.println("OK");
    }
    
    public static String count(List<Pair<String>> list)
    {
        return String.valueOf(list.size());
    }
    
    public static <T> int countGeneric(List<Pair<T>> list)
    {
        return 0;
    }
       
    public static String concat(StringList list)
    {
        return "";
    }
    
    public static <T extends Comparable<T>> List<T> sort(List<T> list)
    {
        Collections.sort(list);
        return list;
    }
    
    static class Pair<T>
    {
        final T _x, _y;
        
        Pair(T x, T y) {
            _x = x;
            _y = y;
        }
        
        T getX() { return _x; }
        T getY() { return _y; }
    }
    
    static class StringList
    {
        private final List<String> list;
        
        StringList(List<String> list)
        {
            this.list = list;
        }
    }
}
