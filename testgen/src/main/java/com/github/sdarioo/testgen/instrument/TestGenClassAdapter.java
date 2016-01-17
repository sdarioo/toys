/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.instrument;

import org.objectweb.asm.*;

public class TestGenClassAdapter 
    extends AccessibleClassVisitor
{
    private final String _methodName;
    
    public TestGenClassAdapter(ClassWriter writer, String methodName) 
    {
        super(writer);
        _methodName = methodName;
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) 
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        
        if ((mv != null) && isMethodAccessible(name, access) && isTransformMethod(name)) {
            mv = new TestGenMethodAdapter(_type, mv, access, name, desc);
        }
        return mv;
    }

    private boolean isTransformMethod(String methodName)
    {
        if ("<cinit>".equals(methodName) || "<init>".equals(methodName)) { //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        }
        
        if (ALL_METHODS.equals(_methodName)) {
            return true;
        }
        return methodName.equals(_methodName);
    }

    static final String ALL_METHODS = "*"; //$NON-NLS-1$
}