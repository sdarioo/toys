/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.asmtest.transform;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceMethodVisitor;


public class RemoveLoggingTransformer
    implements ClassFileTransformer
{
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer)
        throws IllegalClassFormatException 
    {
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(0);

        ClassAdapter adapter = new ClassAdapter(writer);
        
        reader.accept(adapter, 0);
        return writer.toByteArray();
    }
    
    static class ClassAdapter
    extends ClassVisitor
{
    private boolean _bTransform = false;
    
    public ClassAdapter(ClassVisitor writer) 
    {
        super(Opcodes.ASM5, writer);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
            String[] interfaces) 
    {
        super.visit(version, access, name, signature, superName, interfaces);
        if ("com/parasoft/xtest/test/Test".equals(name)) {
            _bTransform = true;
        }
    }
    
    @Override
    public void visitEnd() 
    {
        super.visitEnd();
        if (_bTransform) {
            _bTransform = false;
        }
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) 
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        
        if (_bTransform && (mv != null)) {
            
            mv = new TraceMethodVisitor(mv, new ASMifier(Opcodes.ASM5, "mv", 0) {
                @Override
                public void visitMethodEnd() {
                    print(WRITER);
                    WRITER.flush();
                };
            });
            MethodNode node = new MethodNode(access, name, desc, signature, exceptions);
            mv = new RemoveLoggingMethodAdapter(mv, node);
        }
        return mv;
    }
}

private static PrintWriter WRITER = new PrintWriter(System.out);

}
