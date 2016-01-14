/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder.params.beans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;

import com.github.sdarioo.testgen.logging.Logger;

public final class BeanFactory 
{
    private static BeanFactory INSTANCE = new BeanFactory();
    
    private final Map<Class<?>, Bean> _cache = new HashMap<Class<?>, Bean>();
    
    private BeanFactory() {}
    
    public static BeanFactory getInstance()
    {
        return INSTANCE;
    }
    
    public Bean getBean(Class<?> clazz)
    {
        Bean bean = _cache.get(clazz);
        if (bean == null) {
            
            String name = clazz.getName();
            if (name != null) {
                try {
                    ClassReader reader = new ClassReader(name);
                    BeanIntrospector introspector = new BeanIntrospector();
                    reader.accept(introspector, 0);
                    bean = introspector.getBean();
                } catch (IOException e) {
                    Logger.warn(e.toString());
                }
            }
            if (bean == null) {
                 bean = Bean.UNSUPPORTED;
            }
            _cache.put(clazz, bean);
        }
        
        return bean.isValid() ? bean : null; 
    }

}
