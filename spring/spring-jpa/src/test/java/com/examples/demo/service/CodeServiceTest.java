package com.examples.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.examples.demo.IntegrationTestBase;
import com.examples.demo.model.JProject;
import com.examples.demo.util.SQLCountValidator;

public class CodeServiceTest extends IntegrationTestBase {
	
	@Autowired
	private CodeService service;
	
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
	
	
}
