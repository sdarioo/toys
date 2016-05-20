/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package org.sdarioo.hibernate.test;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sdarioo.hibernate.test.model.Person;
import org.sdarioo.hibernate.test.util.HibernateUtil;
import org.sdarioo.hibernate.test.util.JPAUtil;

public class App 
{
    public static void main(String[] args) 
    {
        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            persons.add(new Person("Person-" + i));
        }
        
        //testHibernate(persons);
        testJPA(persons);
        
        System.out.println("OK");
        System.exit(0);
    }
    
    public static void testHibernate(List<Person> persons)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction t = session.beginTransaction();
        
        for (Person person : persons) {
            session.save(person);
        }
        
        t.commit();
        session.close();
    }
    
    
    private static void testJPA(List<Person> persons)
    {
        EntityManager em = JPAUtil.getEMFactory().createEntityManager();
        
        em.getTransaction().begin();
        
        for (Person person : persons) {
            em.persist(person);
        }
        
        em.getTransaction().commit();
    }
}
