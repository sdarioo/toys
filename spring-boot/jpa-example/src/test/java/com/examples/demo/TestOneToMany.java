package com.examples.demo;

import static org.assertj.core.api.Assertions.assertThat;

import javax.transaction.Transactional;

import org.junit.Test;

import com.examples.demo.model.Parent;


public class TestOneToMany extends AbstractTest {

	@Test
	@Transactional
	public void testFetchQuery() {
		Parent p = new Parent("parent");
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
	
}
