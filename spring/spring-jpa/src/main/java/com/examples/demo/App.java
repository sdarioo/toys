package com.examples.demo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.examples.demo.config.AppConfig;
import com.examples.demo.model.JProject;

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
    	
    	PlatformTransactionManager tx = context.getBean(PlatformTransactionManager.class);
    	EntityManager em = context.getBean(EntityManager.class);

    	List<JProject> projs = new ArrayList<JProject>();
    	for (int j = 0; j < 100; j++) {
	    	for (int i = 0; i < 100; i++) {
	    		projs.add(new JProject("com.parasoft.xtest." + j + ':' + i));
	        }
	    	add(projs, em, tx);
	    	projs.clear();
		}
    	
    	context.close();
        System.out.println("OK");
    }
    
 
    static void add(List<JProject> projs, EntityManager em, PlatformTransactionManager tx)
    {
    	
    	TransactionTemplate template = new TransactionTemplate(tx);
    	template.execute(status -> {
    		//em.setFlushMode(FlushModeType.COMMIT);
    		for (JProject p : projs) {
				em.persist(p);
			}
    		em.flush();
    		em.clear();
    		return null;
    	});
    }
}
