/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.asmtest.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class RemoveLoggingMethodTransformer
    implements IMethodTransformer
{

    @Override
    public void transform(MethodNode method) 
    {
        InsnList insnList = method.instructions;
        ListIterator<AbstractInsnNode> it = insnList.iterator();
        
        List<AbstractInsnNode> toRemove = null;
        while (it.hasNext()) {
            AbstractInsnNode node = it.next();
            if (toRemove == null) {
                if (isGetLoggerInst(node)) {
                    toRemove = new ArrayList<AbstractInsnNode>();
                    toRemove.add(node);
                }
                continue;
            }
            if (isGetLoggerInst(node)) {
                toRemove.clear();
                toRemove.add(node);
                continue;
            }
            toRemove.add(node);
            
            if (isLoggerInst(node)) {
                insnList.insert(toRemove.get(0), new InsnNode(Opcodes.NOP));
                for (AbstractInsnNode nodeToRemove : toRemove) {
                    insnList.remove(nodeToRemove);
                }
                toRemove = null;
            }
        }    
    }
    
    @SuppressWarnings("nls")
    private static boolean isGetLoggerInst(AbstractInsnNode node)
    {
        // <class_name>.LOGGER.warn("message");
        if ((node.getType() == AbstractInsnNode.FIELD_INSN) &&
            (node.getOpcode() == Opcodes.GETSTATIC) &&
            (((FieldInsnNode)node).name.equals("LOGGER")) &&
            (((FieldInsnNode)node).desc.equals("Lcom/parasoft/xtest/logging/api/ParasoftLogger;")) )
        {
            return true;
        } 
        
        // Logger.getLogger().warn("message")
        if ((node.getType() == AbstractInsnNode.METHOD_INSN) &&
            (node.getOpcode() == Opcodes.INVOKESTATIC) &&
            (((MethodInsnNode)node).name.equals("getLogger")) &&
            (((MethodInsnNode)node).desc.equals("()Lcom/parasoft/xtest/logging/api/ParasoftLogger;")) )
        {
            return true;
        }
        
        return false;
    }
    
    @SuppressWarnings("nls")
    private static boolean isLoggerInst(AbstractInsnNode node)
    {
        if ((node.getType() == AbstractInsnNode.METHOD_INSN) &&
            (node.getOpcode() == Opcodes.INVOKEVIRTUAL) &&
            (((MethodInsnNode)node).owner.equals("com/parasoft/xtest/logging/api/ParasoftLogger")) )
        {
            return true;
        }
        return false;
    }

}
