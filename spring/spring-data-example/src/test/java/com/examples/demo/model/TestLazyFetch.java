package com.examples.demo.model;

import static org.junit.Assert.*;

import java.util.Set;

import javax.persistence.Persistence;

import org.junit.Test;

import com.examples.demo.IntegrationTestBase;
import com.examples.demo.util.SQLCountValidator;

public class TestLazyFetch extends IntegrationTestBase {
	
	@Test
	public void testLazyManyToOne() {
		JProject proj = new JProject("proj-1");
		JPackage pkg = new JPackage("pkg-1", proj);
		JClass cls = new JClass("cls-1", pkg);
		
		doInJPA(() -> {
			em.persist(proj);
			em.persist(pkg);
			em.persist(cls);
		});
		
		assertTrue(Persistence.getPersistenceUtil().isLoaded(proj));
		assertTrue(Persistence.getPersistenceUtil().isLoaded(pkg));
		assertTrue(Persistence.getPersistenceUtil().isLoaded(cls));
		
		
		assertFalse(em.contains(proj));
		assertFalse(em.contains(pkg));
		assertFalse(em.contains(cls));
		
		doInJPA(() -> {
			
			SQLCountValidator.reset();
			JClass cl = em.find(JClass.class, cls.getId());
			assertNotNull(cl);
			SQLCountValidator.assertSelectCount(1);
			
			SQLCountValidator.reset();
			JPackage pk = cl.getPackage();
			assertNotNull(pk);
			SQLCountValidator.assertSelectCount(0);
			assertFalse(Persistence.getPersistenceUtil().isLoaded(pk));
			
			pk.getId();
			assertTrue(Persistence.getPersistenceUtil().isLoaded(pk));
			SQLCountValidator.assertSelectCount(1);
		});
	}
	
	@Test
	public void testLazyOneToMany() {
		
		doInJPA(() -> {
			JProject proj = new JProject("proj-1");
			JPackage pkg = new JPackage("pkg-1", proj);
			JClass cls = new JClass("cls-1", pkg);
			
			SQLCountValidator.reset();
			em.persist(proj);
			em.persist(pkg);
			em.persist(cls);
			em.flush();
			em.clear();
			SQLCountValidator.assertInsertCount(3);
			
			SQLCountValidator.reset();
			pkg = em.find(JPackage.class, pkg.getId());
			assertNotNull(pkg);
			SQLCountValidator.assertSelectCount(1);
			
			SQLCountValidator.reset();
			Set<JClass> classes = pkg.getClasses();
			assertNotNull(classes);
			assertFalse(Persistence.getPersistenceUtil().isLoaded(pkg, "classes"));
			SQLCountValidator.assertSelectCount(0);
			
			assertEquals(1, classes.size());
			assertTrue(Persistence.getPersistenceUtil().isLoaded(pkg, "classes"));
			SQLCountValidator.assertSelectCount(1);
		});
		
	}
}
