package com.github.sdarioo.testgen.recorder.params;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

public class BeanIntrospector
    extends ClassVisitor
{
    private final BeanMethodVisitor _methodVisitor;
    
    public BeanIntrospector() 
    {
        super(Opcodes.ASM5);
        _methodVisitor = new BeanMethodVisitor();
    }
    
    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) 
    {
        return super.visitField(access, name, desc, signature, value);
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) 
    {
        Method method = new Method(name, desc);
        
        _methodVisitor.reset();
        MethodVisitor mv = null;
        
        if ("<init>".equals(name)) {
            mv = _methodVisitor;
        } else if (isGetter(method)) {
            mv = _methodVisitor;
        } else if (isSetter(method)) {
            mv = _methodVisitor;
        }
        return mv;
    }
    
    private boolean isGetter(Method method)
    {
        if (!method.getName().startsWith("get")) {
            return false;
        }
        if (method.getArgumentTypes().length > 0) {
            return false;
        }
        if (Type.VOID_TYPE.equals(method.getReturnType())) {
            return false;
        }
        return true;
    }
    
    private boolean isSetter(Method method)
    {
        if (!method.getName().startsWith("set")) {
            return false;
        }
        if (method.getArgumentTypes().length <= 0) {
            return false;
        }
        if (!Type.VOID_TYPE.equals(method.getReturnType())) {
            return false;
        }
        return true;
    }
    
    
    private static class BeanMethodVisitor 
        extends MethodVisitor
    {
        private final List<Getter> _getters;
        private final List<Setter> _setters;
        
        private Getter _get;
        private Setter _set;
        
        BeanMethodVisitor()
        {
            super(Opcodes.ASM5);
            
            _getters = new ArrayList<Getter>();
            _setters = new ArrayList<Setter>();
        }
        
        @Override
        public void visitInsn(int opcode) 
        {
            visitVarInsn(opcode, 0);
        }
        
        @Override
        public void visitVarInsn(int opcode, int var)
        {
            super.visitVarInsn(opcode, var);
            
            // Typical setter instruction sequence
            // ALOAD 0
            // ILOAD 1
            // PUTFIELD <class>.<field> : I
            
            if ((var == 0) && (Opcodes.ALOAD == opcode)) {
                _set = new Setter();
                _get = new Getter();
                
            } else if ((var > 0) && in(opcode, Opcodes.ILOAD, Opcodes.FLOAD, Opcodes.FLOAD, Opcodes.ALOAD)) {
                if (_set != null) {
                    _set.arg = var;
                }
            }
            
            // Typical getter instruction sequence
            // ALOAD 0: this
            // GETFIELD App$Bean.name : String
            // ARETURN
        }
    
        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc)
        {
            super.visitFieldInsn(opcode, owner, name, desc);
            
            if (Opcodes.PUTFIELD == opcode) {
                if ((_set != null) && (_set.arg > 0)) {
                    _set.fieldName = name;
                    _set.fieldDesc = desc;
                    _setters.add(_set);
                }
                _set = null;
            }
            if (Opcodes.GETFIELD == opcode) {
                if (_get != null) {
                    _get.fieldName = name;
                    _get.fieldDesc = desc;
                }
            }
        }
        
        List<Getter> getGetters()
        {
            return _getters;
        }
        
        List<Setter> getSetters()
        {
            return _setters;
        }
        
        void reset()
        {
            _get = null;
            _set = null;
            _getters.clear();
            _setters.clear();
        }
        
        private static boolean in(int code, int... codes)
        {
            for (int c : codes) {
                if (code == c) {
                    return true;
                }
            }
            return false;
        }
    }
    
    private static class BeanInfo
    {
        
    }
    
    private static class Getter
    {
        String fieldName;
        String fieldDesc;
    }
    
    private static class Setter
    {
        int arg; // 1-indexed
        String fieldName;
        String fieldDesc;
    }
}
