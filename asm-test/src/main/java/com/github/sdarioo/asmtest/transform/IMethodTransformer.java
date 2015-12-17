/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.asmtest.transform;

import org.objectweb.asm.tree.MethodNode;

public interface IMethodTransformer 
{
    void transform(MethodNode method);
}
