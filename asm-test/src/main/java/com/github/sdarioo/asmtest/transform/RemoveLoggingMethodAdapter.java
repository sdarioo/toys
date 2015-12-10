package com.github.sdarioo.asmtest.transform;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class RemoveLoggingMethodAdapter
	extends MethodVisitor
{
	private final MethodVisitor _visitor;
	private final MethodNode _node;
	
	public RemoveLoggingMethodAdapter(MethodVisitor mv, MethodNode node) 
	{
		super(Opcodes.ASM5, mv);
		_node = node;
		_visitor = mv;
	}
	
	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) 
	{
		if ((Opcodes.GETSTATIC == opcode) && 
				"java/lang/System".equals(owner) &&
				"out".equals(name) &&
				"Ljava/io/PrintStream;".equals(desc))
		{
			mv = _node;
		}
		super.visitFieldInsn(opcode, owner, name, desc);
	}
	
	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) 
	{
		super.visitMethodInsn(opcode, owner, name, desc, itf);
		
		if ((Opcodes.INVOKEVIRTUAL == opcode) && 
				"java/io/PrintStream".equals(owner) &&
				"println".equals(name) &&
				"(Ljava/lang/String;)V".equals(desc))
		{
			_node.accept(_visitor);
			
			mv = _visitor;
		}
		
	}
	
	@Override
	public void visitEnd() 
	{
		super.visitEnd();
	}
}
