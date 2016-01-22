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
    
    private int _args;
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
        
        _args = newLocal(Type.getType(Object.class));
        _call = newLocal(Type.getType(Call.class));
        
        generateArgumentsArray();
        mv.visitLdcInsn(_owner);
        mv.visitLdcInsn(_method.getName());
        mv.visitLdcInsn(_method.getDescriptor());
        mv.visitMethodInsn(INVOKESTATIC, "com/github/sdarioo/testgen/instrument/InstrumentUtil", "getMethod", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/reflect/Method;", false);
        
        if (_isStatic) {
            mv.visitVarInsn(ALOAD, _args);
            mv.visitMethodInsn(INVOKESTATIC, Call.TYPE_NAME, Call.NEW_CALL_METHOD_NAME, Call.NEW_STATIC_CALL_METHOD_DESC, false);
        } else {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, _args);
            mv.visitMethodInsn(INVOKESTATIC, Call.TYPE_NAME, Call.NEW_CALL_METHOD_NAME, Call.NEW_CALL_METHOD_DESC, false);
        }
        
        mv.visitVarInsn(ASTORE, _call);
    }
    
    @SuppressWarnings("nls")
    @Override
    protected void onMethodExit(int opcode) 
    {
        switch (opcode) {
        case RETURN:
            // call.end()
            mv.visitVarInsn(ALOAD, _call);
            mv.visitMethodInsn(INVOKEVIRTUAL, Call.TYPE_NAME, "end", "()V", false);
            break;
        case IRETURN:
        case FRETURN:
        case ARETURN:
        case LRETURN:
        case DRETURN:
            // Object ret = return object;
            // call.endWithResult(ret);
            if (opcode == LRETURN || opcode == DRETURN) {
                mv.visitInsn(DUP2);
            } else {
                mv.visitInsn(DUP);
            }
            boxIfNeeded(_method.getReturnType());
            int ret = newLocal(Type.getType(Object.class));
            storeLocal(ret);
            mv.visitVarInsn(ALOAD, _call);
            loadLocal(ret);
            mv.visitMethodInsn(INVOKEVIRTUAL, Call.TYPE_NAME, "endWithResult", "(Ljava/lang/Object;)V", false);
            
            break;
        case ATHROW:
            // Throwable exc = thrown exception;
            // call.endWithException(exc);
            int exc = newLocal(Type.getType(Throwable.class));
            mv.visitInsn(DUP);
            mv.visitVarInsn(ASTORE, exc);
            
            mv.visitVarInsn(ALOAD, _call);
            mv.visitVarInsn(ALOAD, exc);
            mv.visitMethodInsn(INVOKEVIRTUAL, Call.TYPE_NAME, "endWithException", "(Ljava/lang/Throwable;)V", false);
            
            break;
        }
    
        // Recorder.getDefault().record(call);
        mv.visitMethodInsn(INVOKESTATIC, Recorder.TYPE_NAME, Recorder.GET_DEFAULT_METHOD_NAME, Recorder.GET_DEFAULT_METHOD_DESC, false);
        mv.visitVarInsn(ALOAD, _call);
        mv.visitMethodInsn(INVOKEVIRTUAL, Recorder.TYPE_NAME, Recorder.RECORD_METHOD_NAME, Recorder.RECORD_METHOD_DESC, false);
        
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
        // Set pessimistic max stack value - instrumentation adds additional 3values to the stack.
        // If ClassWriter uses COMPUTE_MAXS flags than this method will be ignored and max stack values
        // will be computed based on method signature and bytecode instructions.
        super.visitMaxs(maxStack + 3, maxLocals);
    }
    
    @SuppressWarnings("nls")
    private void generateArgumentsArray()
    {
        Type[] argumentTypes = Type.getArgumentTypes(methodDesc);

        mv.visitIntInsn(BIPUSH, argumentTypes.length);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");

        int argIndex = _isStatic ? 0 : 1;
        for (int i = 0; i < argumentTypes.length; i++) {
            Type argumentType = argumentTypes[i];

            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i);
            mv.visitVarInsn(argumentType.getOpcode(ILOAD), argIndex);

            boxIfNeeded(argumentType);

            mv.visitInsn(AASTORE);
            argIndex += argumentType.getSize();
        }

        mv.visitVarInsn(ASTORE, _args);
    }
    
    @SuppressWarnings("nls")
    private void boxIfNeeded(Type type)
    {
        switch (type.getSort()) {
            case Type.BYTE:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Byte",
                        "valueOf",
                        "(B)Ljava/lang/Byte;",
                        false
                );
                break;
            case Type.BOOLEAN:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Boolean",
                        "valueOf",
                        "(Z)Ljava/lang/Boolean;",
                        false
                );
                break;
            case Type.SHORT:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Short",
                        "valueOf",
                        "(S)Ljava/lang/Short;",
                        false
                );
                break;
            case Type.CHAR:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Character",
                        "valueOf",
                        "(C)Ljava/lang/Character;",
                        false
                );
                break;
            case Type.INT:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Integer",
                        "valueOf",
                        "(I)Ljava/lang/Integer;",
                        false
                );
                break;
            case Type.FLOAT:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Float",
                        "valueOf",
                        "(F)Ljava/lang/Float;",
                        false
                );
                break;
            case Type.LONG:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Long",
                        "valueOf",
                        "(J)Ljava/lang/Long;",
                        false
                );
                break;
            case Type.DOUBLE:
                mv.visitMethodInsn(
                        INVOKESTATIC,
                        "java/lang/Double",
                        "valueOf",
                        "(D)Ljava/lang/Double;",
                        false
                );
                break;
        }
    }
    
    
    private static boolean isStatic(int access)
    {
        return (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC; 
    }
    

}
