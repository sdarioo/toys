package com.examples.demo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;

import com.examples.demo.model.JProject;
import com.examples.demo.util.SQLCountValidator;

public class EntityManagerTest extends IntegrationTestBase {

	@PersistenceContext
	private EntityManager em;

	
	@Test
	public void shouldNotPersistOnReadOnlyTxCommit() {
		
		JProject p = addProject();
		try {
			SQLCountValidator.reset();
			doInJPA(() -> {
				JProject pp = em.find(JProject.class, p.getId());
				pp.setName("new-name");
			}, true);
			
			SQLCountValidator.assertUpdateCount(0);
			
		} finally {
			deleteProject(p.getId());
		}
	}
	
	@Test
	public void shouldPersistOnTxCommit() {
		
		JProject p = addProject();
		try {
			SQLCountValidator.reset();
			doInJPA(() -> {
				JProject pp = em.find(JProject.class, p.getId());
				pp.setName("new-name");
			}, false);
			
			SQLCountValidator.assertUpdateCount(1);
			
		} finally {
			deleteProject(p.getId());
		}
	}

	private JProject addProject() {
		JProject p = new JProject("p2");
		doInJPA(() -> {
			em.persist(p);
		});
		return p;
	}
	
	private void deleteProject(Integer id) {
		doInJPA(() -> {
			em.remove(em.find(JProject.class, id));
		});
	}
	
}
