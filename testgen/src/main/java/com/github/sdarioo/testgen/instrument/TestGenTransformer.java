package com.github.sdarioo.testgen.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import com.github.sdarioo.testgen.generator.Generator;
import com.github.sdarioo.testgen.recorder.Call;

public class TestGenTransformer
    implements ClassFileTransformer
{
    private final String _classToTransform;
    private final String _methodToTransform;
    
    public TestGenTransformer(String classToTransform, String methodToTransform)
    {
        _classToTransform = classToTransform;
        _methodToTransform = methodToTransform;
    }
    
    
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain, byte[] classfileBuffer)
                    throws IllegalClassFormatException
    {
        if (!shouldTransform(className)) {
            return classfileBuffer;
        }
        
        Generator.registerShutdownHook();
        
        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(0);
        // TODO - don't instrument interfaces
        TestGenClassAdapter adapter = new TestGenClassAdapter(writer, _methodToTransform);
        
        try {
            // TODO - why need this?? EXPAND_FRAMES
            reader.accept(adapter, ClassReader.EXPAND_FRAMES);
            return writer.toByteArray();
        } catch (Throwable thr) {
            thr.printStackTrace();
        }
        return classfileBuffer;
    }

    
    private boolean shouldTransform(String className)
    {
        return className.endsWith(_classToTransform);
    }
    
    
    private static class TestGenClassAdapter extends ClassVisitor
    {
        private Type _type;
        private final String _methodToTransform;
        
        public TestGenClassAdapter(ClassWriter writer, String methodToTransform) 
        {
            super(Opcodes.ASM5, writer);
            _methodToTransform = methodToTransform;
        }
        
        @Override
        public void visit(int version, int access, String name, String signature, String superName,
                String[] interfaces) 
        {
            super.visit(version, access, name, signature, superName, interfaces);
            _type = Type.getObjectType(name);
        }
        
        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) 
        {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            if ((mv != null) && ("*".equals(_methodToTransform) || name.equals(_methodToTransform))) {
                mv = new TestGenMethodAdapter(_type, mv, access, name, desc);
            }
            return mv;
        }
    }
    
    private static class TestGenMethodAdapter extends AdviceAdapter
    {
        private final Type _owner;
        private final Method _method;
        
        private int _call;
        
        public TestGenMethodAdapter(Type owner, MethodVisitor mv, int access, String name, String desc) 
        {
            super(Opcodes.ASM5, mv, access, name, desc);
            
            _owner = owner;
            _method = new Method(name, desc);
        }
        
        
        @Override
        protected void onMethodEnter() 
        {
            super.onMethodEnter();
            
            // Call call = Call.newCall(Utils.getMethod(clazz, name, desc));
            _call = newLocal(Type.getType(Call.class));
            mv.visitLdcInsn(_owner);
            mv.visitLdcInsn(_method.getName());
            mv.visitLdcInsn(_method.getDescriptor());
            mv.visitMethodInsn(INVOKESTATIC, "com/github/sdarioo/testgen/instrument/Utils", "getMethod", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/reflect/Method;", false);
            mv.visitMethodInsn(INVOKESTATIC, "com/github/sdarioo/testgen/recorder/Call", "newCall", "(Ljava/lang/reflect/Method;)Lcom/github/sdarioo/testgen/recorder/Call;", false);
            mv.visitVarInsn(ASTORE, _call);
            
            // foreach (x : args) {
            //     call.args().add(ParamsFactory.newValue(x));
            // }
            Type[] args = _method.getArgumentTypes();
            
            for (int i = 0; i < args.length; i++) {
                mv.visitVarInsn(ALOAD, _call);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/sdarioo/testgen/recorder/Call", "args", "()Lcom/github/sdarioo/testgen/recorder/ArgList;", false);
                loadArg(i);
                invokeParamsFactoryNewValue(args[i]);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/sdarioo/testgen/recorder/ArgList", "add", "(Lcom/github/sdarioo/testgen/recorder/IParameter;)V", false);
            }
        }
        
        @Override
        protected void onMethodExit(int opcode) 
        {
            switch (opcode) {
            case RETURN:
                // call.setResult(IParameter.VOID)
                mv.visitVarInsn(ALOAD, _call);
                mv.visitFieldInsn(GETSTATIC, "com/github/sdarioo/testgen/recorder/IParameter", "VOID", "Lcom/github/sdarioo/testgen/recorder/IParameter;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/sdarioo/testgen/recorder/Call", "setResult", "(Lcom/github/sdarioo/testgen/recorder/IParameter;)V", false);
                break;
            case IRETURN:
            case FRETURN:
            case ARETURN:
            case LRETURN:
            case DRETURN:
                // Object ret = return object;
                // call.setResult(ret);
                Type returnType = _method.getReturnType();
                int ret = newLocal(returnType);
                mv.visitInsn(DUP);
                storeLocal(ret);
                
                mv.visitVarInsn(ALOAD, _call);
                loadLocal(ret);
                invokeParamsFactoryNewValue(returnType);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/sdarioo/testgen/recorder/Call", "setResult", "(Lcom/github/sdarioo/testgen/recorder/IParameter;)V", false);
                
                break;
            case ATHROW:
                // Throwable exc = thrown exception;
                // call.setException(exc);
                int exc = newLocal(Type.getType(Throwable.class));
                mv.visitInsn(DUP);
                mv.visitVarInsn(ASTORE, exc);
                
                mv.visitVarInsn(ALOAD, _call);
                mv.visitVarInsn(ALOAD, exc);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/sdarioo/testgen/recorder/Call", "setException", "(Ljava/lang/Throwable;)V", false);
                
                break;
            }

            // Recorder.getDefault().record(call);
            mv.visitMethodInsn(INVOKESTATIC, "com/github/sdarioo/testgen/recorder/Recorder", "getDefault", "()Lcom/github/sdarioo/testgen/recorder/Recorder;", false);
            mv.visitVarInsn(ALOAD, _call);
            mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/sdarioo/testgen/recorder/Recorder", "record", "(Lcom/github/sdarioo/testgen/recorder/Call;)V", false);
            
            super.onMethodExit(opcode);
        }
        
        
        @Override
        public void visitMaxs(int maxStack, int maxLocals) 
        {
            // TODO
            super.visitMaxs(maxStack + 4, maxLocals);
        }
        
        private void invokeParamsFactoryNewValue(Type type)
        {
            String desc = isPrimitive(type) ? 
                    "(" + type.getDescriptor() + ")Lcom/github/sdarioo/testgen/recorder/IParameter;": 
                    "(Ljava/lang/Object;)Lcom/github/sdarioo/testgen/recorder/IParameter;";
            
            mv.visitMethodInsn(INVOKESTATIC, "com/github/sdarioo/testgen/recorder/params/ParamsFactory", "newValue", desc, false);
        }
        
        
        private static boolean isPrimitive(Type t)
        {
            return t.getSort() <= 8;
        }
    }
    
}
