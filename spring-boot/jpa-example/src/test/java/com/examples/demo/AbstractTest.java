package com.examples.demo;

import java.util.concurrent.ExecutorService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;


@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class AbstractTest {
	
	@PersistenceContext
	protected EntityManager em;
	
	@Autowired
	protected PlatformTransactionManager txManager;
	
	protected AbstractTest() {
	}
	
	protected void runInJPA(Runnable r) {
		new TransactionTemplate(txManager).execute(status -> {
			try {
			r.run();
			} catch (Throwable t) { t.printStackTrace(); }
			return null;
		});
	}
	
	protected void runInJPA(ExecutorService executor, Runnable r) {
		executor.submit(() -> runInJPA(r));
	}
}
