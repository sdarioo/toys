package com.examples.demo;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.AbstractEnvironment;

import com.examples.demo.config.AppConfig;
import com.examples.demo.model.JPackage;
import com.examples.demo.model.JProject;
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
    	JProject project = service.addProject("xtest");
    	JPackage xtest = service.addPackage("com.parasoft.xtest", project, null);
    	JPackage util = service.addPackage("com.parasoft.xtest.util", project, xtest);
    	
    	service.getChildPackages(xtest);
    	
    	context.close();
        System.out.println("OK");
    }
    
    
}
