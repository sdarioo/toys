package com.examples.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import javax.transaction.Transactional;

import org.junit.Test;

import com.examples.demo.model.Child;
import com.examples.demo.model.Parent;


public class TestOneToMany extends AbstractTest {

	@Test
	@Transactional
	public void testFetchQuery() {
		Parent p = new Parent("parent-1");
		p.addChild("child-1");
		p.addChild("child-2");
		
		em.persist(p);
		em.flush();
		em.clear();
		
		p = em.getReference(Parent.class, p.getId());
		assertThat(p).isNotNull();
		
		//p.getChildren().clear();
		em.remove(p);
		em.flush();
	}
	
	@Test
	@Transactional
	public void testCascadeType() {
		
		Parent p = new Parent("parent-2");
		em.persist(p);
		em.flush();
		
		Child child = p.addChild("child-1");
		em.flush();
		em.clear();
		
		p = em.find(Parent.class, p.getId());
		assertEquals(1, p.getChildren().size());
		
		p.removeChild(child);
		em.flush();
		em.clear();
		
		p = em.find(Parent.class, p.getId());
		assertEquals(1, p.getChildren().size());
	}
	
}
