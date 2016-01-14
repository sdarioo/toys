/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.instrument;

import org.objectweb.asm.*;

public class TestGenClassAdapter 
    extends ClassVisitor
{
    private Type _type;
    private boolean _isTransformClass;
    
    private final String _methodName;
    
    public TestGenClassAdapter(ClassWriter writer, String methodName) 
    {
        super(Opcodes.ASM5, writer);
        _methodName = methodName;
    }
    
    @Override
    public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) 
    {
        super.visit(version, access, name, signature, superName, interfaces);
        
        _type = Type.getObjectType(name);
        _isTransformClass = isTransformClass(access);
    }
    
    
    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) 
    {
        super.visitInnerClass(name, outerName, innerName, access);
        
        Type type = Type.getObjectType(name);
        if (type.equals(_type)) {
            _isTransformClass = isTransformClass(access);
        }
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) 
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ((mv != null) && 
                _isTransformClass &&
                isTransformMethod(name, access)) 
        {
            mv = new TestGenMethodAdapter(_type, mv, access, name, desc);
        }
        return mv;
    }
    
    private boolean isTransformClass(int access)
    {
        int[] excludeFlags = {
                Opcodes.ACC_PRIVATE,
                Opcodes.ACC_INTERFACE,
                Opcodes.ACC_SYNTHETIC,
                Opcodes.ACC_ANNOTATION,
                Opcodes.ACC_ENUM
            };
        if (InstrumentUtil.isFlagSet(access, excludeFlags)) {
            return false;
        }
        return true;
    }
    
    private boolean isTransformMethod(String methodName, int access)
    {
        int[] excludeFlags = {
            Opcodes.ACC_PRIVATE,
            Opcodes.ACC_BRIDGE,
            Opcodes.ACC_NATIVE,
            Opcodes.ACC_ABSTRACT,
            Opcodes.ACC_SYNTHETIC
        };
        if (InstrumentUtil.isFlagSet(access, excludeFlags)) {
            return false;
        }
        if (ALL_METHODS.equals(_methodName)) {
            return true;
        }
        return methodName.equals(_methodName);
    }
    

    
    static final String ALL_METHODS = "*"; //$NON-NLS-1$
}
