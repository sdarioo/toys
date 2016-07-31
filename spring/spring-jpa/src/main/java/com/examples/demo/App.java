package com.examples.demo;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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
	
    public static void main( String[] args )
    {
    	AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    	
    	ProjectRepository repo = context.getBean(ProjectRepository.class);
    	
    	DataSource ds = context.getBean("dataSource", DataSource.class);
    	EntityManager em = context.getBean(EntityManager.class);
    	PlatformTransactionManager tx = context.getBean(PlatformTransactionManager.class);

    	long t = System.currentTimeMillis();
    	
    	List<JProject> projs = new ArrayList<JProject>();
    	for (int j = 0; j < 100; j++) {
	    	for (int i = 0; i < 1000; i++) {
	    		JProject p = new JProject("com.parasoft.xtest." + j + ':' + i);
	    		p.setId((j*1000)+i);
				projs.add(p);
	        }
	    	addWithJdbcTemplate(projs, ds, tx);
	    	//add(projs, em, tx);
	    	//repo.saveAndFlush(projs);
	    	projs.clear();
		}

    	System.err.println("Total: " + (System.currentTimeMillis() - t));
    	
    	context.close();
    }
    
    static void addWithJdbcTemplate(List<JProject> projs, DataSource ds, PlatformTransactionManager tx)
    {
    	String insert = "INSERT INTO projects(id, name) VALUES (:id, :name)";
    	
    	NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(ds);
    	
    	SqlParameterSource[] batchArgs = projs.stream()
    			.map(BeanPropertySqlParameterSource::new)
    			.toArray(SqlParameterSource[]::new);
    	
    	runTx(tx, () -> template.batchUpdate(insert, batchArgs));
	}
 
    static void add(List<JProject> projs, EntityManager em, PlatformTransactionManager tx)
    {
    	Runnable r = () -> {
    		for (JProject p : projs) {
				em.persist(p);
			}
    		em.flush();
    		em.clear();
    	};
    	runTx(tx, r);
    }
    
    static void runTx(PlatformTransactionManager tx, Runnable runnable)
    {
    	TransactionTemplate template = new TransactionTemplate(tx);
    	template.execute(status -> {
    		runnable.run();
    		return null;
    	});
    }
    
}
