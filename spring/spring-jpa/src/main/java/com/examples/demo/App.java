package com.examples.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.AbstractEnvironment;

import com.examples.demo.config.AppConfig;
import com.examples.demo.model.JProject;
import com.examples.demo.repository.ProjectRepository;
import com.examples.demo.service.CodeService;

/**
 * Hello world!
 */
public class App 
{
	public static final String DEV_PROFILE = "dev";
	public static final String TEST_PROFILE = "test";
	
    public static void main( String[] args )
    {
    	System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, DEV_PROFILE);
    	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    	
    	CodeService service = context.getBean(CodeService.class);
    	ProjectRepository repo = context.getBean(ProjectRepository.class);
    	
    	List<JProject> projs = new ArrayList<JProject>();
    	for (int i = 0; i < 10; i++) {
    		projs.add(new JProject("com.parasoft.xtest." + i));
        }
    	repo.save(projs);
    	
    	context.close();
        System.out.println("OK");
    }
    
    
}
