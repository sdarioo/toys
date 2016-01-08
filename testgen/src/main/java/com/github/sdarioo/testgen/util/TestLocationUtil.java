/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.util;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;

public class TestLocationUtil 
{
    
    @SuppressWarnings("nls")
    public static File getTestLocation(Class<?> testedClass)
    {
        String pkgPath = testedClass.getPackage().getName().replace('.', '/');
        
        CodeSource codeSource = testedClass.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL url = codeSource.getLocation();
            File loc = toFile(url);
            String path = loc.getAbsolutePath().replace('\\', '/');
            if (loc.isDirectory()) {
                // Maven
                if (path.endsWith("/target/classes")) {
                    File testSrcDir = new File(loc.getParentFile().getParentFile(), "src/test/java");
                    if (testSrcDir.isDirectory()) {
                        return new File(testSrcDir, pkgPath);
                    }
                }
                // Eclipse
                if (path.endsWith("/bin") && isEclipseProject(loc.getParentFile())) {
                    File testsDir = getEclipseTestProject(loc.getParentFile());
                    File testSrcDir = new File(testsDir, "src");
                    if (testSrcDir.isDirectory()) {
                        return new File(testSrcDir, pkgPath);
                    }
                }
            }
        }
        
        return null;
    }
    
    private static boolean isEclipseProject(File dir)
    {
        return new File(dir, ".project").isFile(); //$NON-NLS-1$
    }
    
    private static File getEclipseTestProject(File projectDir)
    {
        String name = projectDir.getName();
        File testsDir = new File(projectDir.getParentFile(), name + ".tests"); //$NON-NLS-1$
        if (testsDir.isDirectory()) {
            return testsDir;
        }
        return projectDir;
    }
    
    public static File toFile(URL url)
    {
        try {
            String path = url.toURI().getPath();
            return new File(path);
        } catch (URISyntaxException e) {
            return new File(url.getPath());
        }
    }
}
