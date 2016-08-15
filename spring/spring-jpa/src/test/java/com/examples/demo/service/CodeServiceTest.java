package com.examples.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.examples.demo.IntegrationTestBase;
import com.examples.demo.model.JPackage;
import com.examples.demo.model.JProject;
import com.examples.demo.util.SQLCountValidator;
import com.examples.demo.util.SQLExecutionListener;

public class CodeServiceTest extends IntegrationTestBase {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CodeServiceTest.class);
	
	@Autowired
	private CodeService service;
	

	@Before
	public void cleanUp() {
		doInJPA(() -> {
			LOGGER.info(">>> cleaning db started ...");
			List<JPackage> packages = service.getPackages();
			List<JProject> projects = service.getProjects();
			
			for (JPackage packge : packages) {
				em.remove(packge);
			}
			for (JProject project : projects) {
				em.remove(project);
			}
			LOGGER.info("<<< cleaning db finished.");
		});
	}
	
	@Test
	public void shouldAddProject() {
		
		SQLCountValidator.reset();
		
		JProject project1 = service.addProject("ProjectA");
		assertThat(project1).isNotNull();
		assertThat(project1.isNew()).isFalse();
		
		SQLCountValidator.assertSelectCount(0);
		SQLCountValidator.assertInsertCount(1);
	}
	
	@Test
	public void shouldFindAllProjects() {
		
		SQLCountValidator.reset();
		
		List<JProject> projects = service.getProjects();
		assertThat(projects).isEmpty();
		
		service.addProject("ProjectA");
		service.addProject("ProjectB");
		
		SQLCountValidator.assertSelectCount(1);
		SQLCountValidator.reset();
		
		projects = service.getProjects();
		assertThat(projects).hasSize(2);
		
		SQLCountValidator.assertSelectCount(1);
	}
	
	@Test
	public void shouldExecuteSingleQueryToGetPackages() {
		
		// Given
		JProject project = service.addProject("ProjectA");
		service.addPackage("pkgA", project, null);
		service.addPackage("pkgB", project, null);
		
		// When
		SQLCountValidator.reset();
		SQLExecutionListener.getDefault().startCollecting();
		List<JPackage> packages = em.createQuery("from JPackage", JPackage.class).getResultList();
		SQLExecutionListener.getDefault().stopCollecting();
		
		// Then
		assertEquals(2, packages.size());
		SQLExecutionListener.getDefault().logAndReset();
		SQLCountValidator.assertSelectCount(1);
	}
	
	
}
