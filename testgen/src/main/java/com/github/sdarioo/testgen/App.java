/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen;

import java.util.Properties;

public class App 
{
    public static void main(String[] args) 
    {
        for (int i = 0; i < 10; i++) {
            Properties p = new Properties();
            p.setProperty("key-"+i, "value-"+i);
            
            concat(p);
            add(i, 10 - i);
        }
    }
    
    public static int add(int x, int y)
    {
        int result = x + y;
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

}
