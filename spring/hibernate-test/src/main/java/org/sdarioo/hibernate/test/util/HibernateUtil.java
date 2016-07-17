/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package org.sdarioo.hibernate.test.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil 
{
    private static SessionFactory sessionFactory = buildSessionFactory();
    
    private static SessionFactory buildSessionFactory()
    {
       try {
           if (sessionFactory == null) {
               Configuration configuration = new Configuration().configure();
               StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
               serviceRegistryBuilder.applySettings(configuration.getProperties());
               ServiceRegistry serviceRegistry = serviceRegistryBuilder.build();
               sessionFactory = configuration.buildSessionFactory(serviceRegistry);
           }
           return sessionFactory;
       } catch (Throwable ex) {
           throw new ExceptionInInitializerError(ex);
       }
    }
  
    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }
  
    public static void shutdown()
    {
        getSessionFactory().close();
    }
}
