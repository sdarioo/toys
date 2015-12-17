/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.asmtest.transform;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

public class TransformingClassAdapter
    extends ClassVisitor
{
    private String _sClassName;
    private boolean _bVerify = false;
    private final List<IMethodTransformer> _methodTransformers;
    
    public TransformingClassAdapter(ClassVisitor writer) 
    {
        super(Opcodes.ASM5, writer);
        _methodTransformers = new ArrayList<IMethodTransformer>();
    }
    
    public void setVerify(boolean bVerify)
    {
        _bVerify = bVerify;
    }
    
    public void addMethodTransformer(IMethodTransformer transformer)
    {
        _methodTransformers.add(transformer);
    }

    @Override
    public void visit(int version, 
            int access, 
            String name, 
            String signature, 
            String superName,
            String[] interfaces) 
    {
        _sClassName = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }
    
    @Override
    public MethodVisitor visitMethod(int access, 
            String name, 
            String desc, 
            String signature, 
            String[] exceptions) 
    {
        final MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);
        if (visitor == null) {
            return null;
        }
        MethodNode method = new MethodNode(Opcodes.ASM5, access, name, desc, signature, exceptions) {
            @Override
            public void visitEnd()
            {
                super.visitEnd();
                for (IMethodTransformer transformer : _methodTransformers) {
                    transformer.transform(this);
                    if (_bVerify) {
                        verify(_sClassName, this, transformer.getClass().getName());
                    }
                }
                
                accept(visitor);
            }
        };
        return method;
    }
    
    @Override
    public void visitEnd() 
    {
        super.visitEnd();
    }
    
    @SuppressWarnings("nls")
    private static void verify(String owner, MethodNode method, String sTransformerName)
    {
        Analyzer<BasicValue> analyzer = new Analyzer<>(new BasicVerifier());
        try {
            analyzer.analyze(owner, method);
        } catch (AnalyzerException e) {
            String msg = MessageFormat.format("Method transforming failed: Class={0}, Method={1}, Transformer={2}", 
                    owner,
                    method.name, 
                    sTransformerName);
            System.err.println(msg);
        }
    }
}
