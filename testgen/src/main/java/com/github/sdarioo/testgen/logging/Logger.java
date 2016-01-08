/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.logging;

public class Logger 
{

    public static void info(String msg)
    {
        System.err.println("[INFO]" + msg); //$NON-NLS-1$
    }
    
    public static void warn(String msg)
    {
        System.err.println("[WARN]" + msg); //$NON-NLS-1$
    }
    
    public static void error(String msg)
    {
        System.err.println("[ERROR]" + msg); //$NON-NLS-1$
    }
}
