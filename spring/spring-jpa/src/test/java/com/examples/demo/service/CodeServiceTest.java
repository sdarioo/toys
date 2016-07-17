package com.examples.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.examples.demo.App;
import com.examples.demo.config.AppConfig;
import com.examples.demo.model.JProject;

@ContextConfiguration(classes = AppConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(App.TEST_PROFILE)
public class CodeServiceTest {
	
	@Autowired
	private CodeService service;
	
	@Test
	public void shouldAddProject() {
		JProject project1 = service.addProject("ProjectA");
		assertThat(project1).isNotNull();
		assertThat(project1.isNew()).isFalse();
	}
	
	@Test
	public void shouldFindAllProjects() {
		List<JProject> projects = service.getProjects();
		assertThat(projects).isEmpty();
		
		service.addProject("ProjectA");
		service.addProject("ProjectB");
		
		projects = service.getProjects();
		assertThat(projects).hasSize(2);
	}
	
	
}
