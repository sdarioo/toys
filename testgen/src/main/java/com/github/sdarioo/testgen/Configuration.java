/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen;

public final class Configuration 
{
    private int _maxCollectionSize = 20;
    private int _maxStringLength = 200;
    
    private boolean _backgroundGeneration = true;
    
    private int _maxCalls = 10;
    
    private static Configuration INSTANCE = new Configuration();
    
    public static Configuration getDefault()
    {
        return INSTANCE;
    }
    
    public int getMaxCalls()
    {
        return _maxCalls;
    }
    
    public int getMaxCollectionSize()
    {
        return _maxCollectionSize;
    }
    
    public int getMaxStringLength()
    {
        return _maxStringLength;
    }
    
    public boolean isBackgroundGenerationEnabled()
    {
        return _backgroundGeneration;
    }
}
