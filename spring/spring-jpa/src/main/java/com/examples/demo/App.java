package com.examples.demo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.examples.demo.config.AppConfig;
import com.examples.demo.model.JProject;
import com.examples.demo.repository.ProjectRepository;

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
    	
    	ProjectRepository repo = context.getBean(ProjectRepository.class);
    	
    	EntityManager em = context.getBean(EntityManager.class);
    	PlatformTransactionManager tx = context.getBean(PlatformTransactionManager.class);

    	long t = System.currentTimeMillis();
    	
    	List<JProject> projs = new ArrayList<JProject>();
    	for (int j = 0; j < 1001; j++) {
	    	for (int i = 0; i < 100; i++) {
	    		projs.add(new JProject("com.parasoft.xtest." + j + ':' + i));
	        }
	    	repo.saveAndFlush(projs);
	    	projs.clear();
		}

    	System.err.println("Total: " + (System.currentTimeMillis() - t));
    	
    	context.close();
    }
    
 
    static void add(List<JProject> projs, EntityManager em, PlatformTransactionManager tx)
    {
    	TransactionTemplate template = new TransactionTemplate(tx);
    	template.execute(status -> {
    		for (JProject p : projs) {
				em.persist(p);
			}
    		em.flush();
    		em.clear();
    		return null;
    	});
    }
}
