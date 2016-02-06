/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.instrument;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import com.github.sdarioo.testgen.recorder.Recorder;

public class TestGenMethodAdapter2 
    extends AdviceAdapter
{
    private final Type _owner;
    private final Method _method;
    private final boolean _isStatic;
    private final String[] _paramNames; 
    
    private final Label startFinallyLabel = new Label();

    public TestGenMethodAdapter2(Type owner, MethodVisitor mv, int access, String name, String desc) 
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
    
    @Override
    public void visitMaxs(int maxStack, int maxLocals)
    {
        Label endFinallyLabel = new Label();
        super.visitTryCatchBlock(startFinallyLabel, endFinallyLabel, endFinallyLabel, null);
        super.visitLabel(endFinallyLabel);
        onFinally(ATHROW);
        super.visitInsn(ATHROW);
        super.visitMaxs(maxStack, maxLocals);
    }
    
    @Override
    protected void onMethodEnter()
    {
    	super.visitLabel(startFinallyLabel);

    	int argIndex = generateArgumentsArray();
    	
    	 mv.visitLdcInsn(_owner);
         mv.visitLdcInsn(_method.getName());
         mv.visitLdcInsn(_method.getDescriptor());
         mv.visitMethodInsn(INVOKESTATIC, 
        		 "com/github/sdarioo/testgen/instrument/RecorderAPI", 
        		 "getMethod", 
        		 "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/reflect/Method;", false);
    	
        if (_isStatic) {
        	super.visitInsn(ACONST_NULL);
        } else {
        	super.visitVarInsn(ALOAD, 0);
        }
        super.visitVarInsn(ALOAD, argIndex);    
   
        super.visitMethodInsn(
                INVOKESTATIC,
                "com/github/sdarioo/testgen/instrument/RecorderAPI",
                "methodBegin",
                "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)V",
                false
        );

    }
    
    
    @Override
    protected void onMethodExit(int opcode)
    {
        if (opcode != ATHROW) {
            onFinally(opcode);
        }
    }
    
    private void onFinally(int opcode)
    {

        if (opcode == ATHROW) {

            super.visitInsn(DUP);

            super.visitMethodInsn(
                    INVOKESTATIC,
                    "com/github/sdarioo/testgen/instrument/RecorderAPI",
                    "methodEndWithException",
                    "(Ljava/lang/Throwable;)V",
                    false
            );

        } else if (opcode == RETURN) {

            super.visitMethodInsn(
                    INVOKESTATIC,
                    "com/github/sdarioo/testgen/instrument/RecorderAPI",
                    "methodEnd",
                    "()V",
                    false
            );

        } else {

            if (opcode == LRETURN || opcode == DRETURN) {
                super.visitInsn(DUP2);
            } else {
                super.visitInsn(DUP);
            }

            box(Type.getReturnType(methodDesc));

            super.visitMethodInsn(
                    INVOKESTATIC,
                    "com/github/sdarioo/testgen/instrument/RecorderAPI",
                    "methodEndWithResult",
                    "(Ljava/lang/Object;)V",
                    false
            );

        }

    }
    
    @Override
    public void visitEnd() 
    {
        super.visitEnd();
        Recorder.getDefault().setArgumentNames(_owner, _method, _paramNames);
    }
  
  
    @SuppressWarnings("nls")
    private int generateArgumentsArray()
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

        mv.visitVarInsn(ASTORE, argIndex);
        return argIndex;
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
