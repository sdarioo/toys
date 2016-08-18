package com.examples.demo.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.examples.demo.IntegrationTestBase;
import com.examples.demo.util.SQLCountValidator;

public class TestOneToMany extends IntegrationTestBase {

	@Test
	public void testAddChild() {
		doInJPA(() -> {
			JProject pr = new JProject("pr");
			JPackage pk = new JPackage("pk", pr);
			
			JClass cl = new JClass("cl", pk);
			
			SQLCountValidator.reset();
			em.persist(pr);
			em.persist(pk);
			em.flush();
			SQLCountValidator.assertInsertCount(2);
			
			assertFalse(em.contains(cl));
			
		});
	}
}
