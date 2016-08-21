package com.examples.demo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.examples.demo.model.Product;

public class App {

	public static final String UNIT_NAME = "TEST-UNIT";
	
	public static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory(UNIT_NAME);
	
	public static void main(String[] args) {
		
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		
		Product p = new Product("box", "box desc");
		em.persist(p);
		System.out.println(Persistence.getPersistenceUtil().isLoaded(p));
		
		em.getTransaction().commit();
		em.close();
		
		EMF.close();
	}

}
