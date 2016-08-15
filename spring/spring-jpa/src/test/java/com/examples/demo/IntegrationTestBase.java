package com.examples.demo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.examples.demo.config.AppConfig;

@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.TEST_PROFILE)
@TestPropertySource(properties={"active.database=hsql"})
public abstract class IntegrationTestBase {
	
	
	@Autowired
	protected PlatformTransactionManager txManager;
	
	@PersistenceContext
	protected EntityManager em;
	
	protected IntegrationTestBase() {}
	
	
	protected void doInJPA(Runnable runnable) {
		doInJPA(runnable, false);
	}
	
	protected void doInJPA(Runnable runnable, boolean readOnly) {
		
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setReadOnly(readOnly);
		
		TransactionTemplate template = new TransactionTemplate(txManager, definition);
		template.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				runnable.run();
				return null;
			}
		});
	}

}
