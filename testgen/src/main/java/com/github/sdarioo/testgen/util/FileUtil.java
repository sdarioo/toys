/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;

public final class FileUtil 
{
    private FileUtil() {}
    
    public static Pair<String, String> getNameWithExtension(String fileName)
    {
        String base = fileName;
        String ext = null;
        int idx = fileName.lastIndexOf('.');
        if (idx > 0) {
            base = fileName.substring(0, idx);
            ext = fileName.substring(idx + 1);
        }
        return Pair.of(base, ext);
    }
    
    public static void write(File file, String content)
        throws IOException
    {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(content);
        } finally {
            if (writer != null) { try { writer.close(); } catch (IOException e) {} }
        }
    }
    
    
    
}
