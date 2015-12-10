package com.github.sdarioo.asmtest.utils;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;


public final class ClassUtils 
{
   private ClassUtils() { }

   private static Method DEFINE_CLASS;
   private static final ProtectionDomain PROTECTION_DOMAIN;

   static {
      PROTECTION_DOMAIN = AccessController.doPrivileged(new PrivilegedAction<ProtectionDomain>() {
         public ProtectionDomain run() {
            return ClassUtils.class.getProtectionDomain();
         }
      });

      AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            try {
               Class<?> loader = Class.forName("java.lang.ClassLoader"); // JVM crash w/o this
               DEFINE_CLASS = loader.getDeclaredMethod("defineClass",
                  new Class[]{ String.class,
                     byte[].class,
                     Integer.TYPE,
                     Integer.TYPE,
                     ProtectionDomain.class });
               DEFINE_CLASS.setAccessible(true);
            } catch (ClassNotFoundException e) {
               throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
               throw new RuntimeException(e);
            }
            return null;
         }
      });
   }

   @SuppressWarnings("unchecked")
   public static <T> Class<T> defineClass(String className, byte[] b, ClassLoader loader)
      throws Exception 
   {
      Object[] args = new Object[]{className, b, new Integer(0), new Integer(b.length), PROTECTION_DOMAIN };
      Class<T> c = (Class<T>) DEFINE_CLASS.invoke(loader, args);
      // Force static initializers to run.
      Class.forName(className, true, loader);
      return c;
   }

   @SuppressWarnings("unchecked")
   public static <T> Class<T> getExistingClass(ClassLoader classLoader, String className)
   {
      try {
         return (Class<T>) Class.forName(className, true, classLoader);
      } catch (ClassNotFoundException e) {
         return null;
      }
   }
}
