/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.generator.source;

public class ResourceFile 
{
    private final String _fileName;
    private final String _content;
    
    public ResourceFile(String fileName, String content)
    {
        _fileName = fileName;
        _content = content;
    }
    
    public String getFileName()
    {
        return _fileName;
    }
    
    public String getContent()
    {
        return _content;
    }
}
