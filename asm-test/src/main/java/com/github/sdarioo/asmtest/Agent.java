/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.asmtest;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.github.sdarioo.asmtest.transform.RemoveLoggingMethodTransformer;
import com.github.sdarioo.asmtest.transform.TransformingClassAdapter;

public class Agent 
{
    public static void premain(String args, Instrumentation inst) 
    {
        RemoveLoggingTransformer transformer = new RemoveLoggingTransformer();
        inst.addTransformer(transformer);
    }
    
    @SuppressWarnings("nls")
    private static class RemoveLoggingTransformer
        implements ClassFileTransformer
    {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException 
        {
            if (!className.startsWith("com/parasoft/xtest")) {
                return classfileBuffer;
            }
            
            ClassReader reader = new ClassReader(classfileBuffer);
            ClassWriter writer = new ClassWriter(0);
            TransformingClassAdapter adapter = new TransformingClassAdapter(writer);
            adapter.addMethodTransformer(new RemoveLoggingMethodTransformer());
            adapter.setVerify(true);
            
            try {
                reader.accept(adapter, 0);
                return writer.toByteArray();
            } catch (Throwable thr) {
                thr.printStackTrace();
            }
            return classfileBuffer;
        }
        
        
    }

}


