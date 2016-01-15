/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.instrument;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import com.github.sdarioo.testgen.recorder.Call;
import com.github.sdarioo.testgen.recorder.Recorder;

public class TestGenMethodAdapter 
    extends AdviceAdapter
{
    private final Type _owner;
    private final Method _method;
    private final boolean _isStatic;
    private final String[] _paramNames; 
    
    private int _call;
    
    public TestGenMethodAdapter(Type owner, MethodVisitor mv, int access, String name, String desc) 
    {
        super(Opcodes.ASM5, mv, access, name, desc);
        
        _isStatic = isStatic(access);
        _owner = owner;
        _method = new Method(name, desc);
        _paramNames = new String[_method.getArgumentTypes().length];
    }
    
    @Override
    public void visitLocalVariable(String name, String desc, String signature,
            Label start, Label end, int index) 
    {
        super.visitLocalVariable(name, desc, signature, start, end, index);

        int paramIndex = _isStatic ? index : index - 1;
        if ((paramIndex >= 0) && (paramIndex < _paramNames.length)) {
            _paramNames[paramIndex] = name;
        }
    }
    
    
    @SuppressWarnings("nls")
    @Override
    protected void onMethodEnter() 
    {
        super.onMethodEnter();
        
        // Call call = Call.newCall(Utils.getMethod(clazz, name, desc));
        _call = newLocal(Type.getType(Call.class));
        mv.visitLdcInsn(_owner);
        mv.visitLdcInsn(_method.getName());
        mv.visitLdcInsn(_method.getDescriptor());
        mv.visitMethodInsn(INVOKESTATIC, "com/github/sdarioo/testgen/instrument/InstrumentUtil", "getMethod", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/reflect/Method;", false);
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
    
    @SuppressWarnings("nls")
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
    public void visitEnd() 
    {
        super.visitEnd();
        Recorder.getDefault().setArgumentNames(_owner, _method, _paramNames);
    }
    
    
    @Override
    public void visitMaxs(int maxStack, int maxLocals) 
    {
        // TODO
        super.visitMaxs(maxStack + 4, maxLocals);
    }
    
    @SuppressWarnings("nls")
    private void invokeParamsFactoryNewValue(Type type)
    {
        String desc = isPrimitive(type) ? 
                "(" + type.getDescriptor() + ")Lcom/github/sdarioo/testgen/recorder/IParameter;": 
                "(Ljava/lang/Object;)Lcom/github/sdarioo/testgen/recorder/IParameter;";
        
        mv.visitMethodInsn(INVOKESTATIC, "com/github/sdarioo/testgen/recorder/params/ParamsFactory", "newValue", desc, false);
    }
    
    private static boolean isStatic(int access)
    {
        return (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC; 
    }
    
    private static boolean isPrimitive(Type t)
    {
        return t.getSort() <= 8;
    }
}
