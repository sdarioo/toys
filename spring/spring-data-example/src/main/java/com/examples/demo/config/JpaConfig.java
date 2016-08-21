package com.examples.demo.config;

import java.text.MessageFormat;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("com.examples.demo.repository")
@PropertySource("classpath:data-access.properties")
public class JpaConfig {
	
	@Autowired
	private Environment env;
	
	private static final String PERSISTENCE_UNIT_NAME = "demoPersistenceUnit";
	
	// Active database property
	private static final String ACTIVE_DATABASE = "active.database";
	
	// Properties prefixed with active database
	private static final String DRIVER_CLASS_NAME = "{0}.driverClassName";
	private static final String URL = "{0}.url";
	private static final String USERNAME = "{0}.username";
	private static final String PASSWORD = "{0}.password";
	private static final String DATABASE = "{0}.database";
	private static final String INIT_DB = "{0}.initDB";

	
	@PostConstruct
	private void init()	{
		String database = activeDatabase();
		Objects.requireNonNull(database);
		
		initDB();
	}
	
	@Bean(name="dataSource", destroyMethod="close")
	public DataSource dataSource() {
		return DataSourceBuilder.buildDataSource(
				getPrefixedProperty(DRIVER_CLASS_NAME), 
				getPrefixedProperty(URL),
				getPrefixedProperty(USERNAME),
				getPrefixedProperty(PASSWORD));
	}
	
	@Bean(name="entityManagerFactory")
	public AbstractEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(getPrefixedProperty(DATABASE, Database.class));
		
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		emf.setDataSource(dataSource);
		emf.setJpaVendorAdapter(vendorAdapter);
		return emf;
	}
	
	@Bean(name="transactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(emf);
        return jpaTransactionManager;
	}

	private String activeDatabase() {
		return env.getProperty(ACTIVE_DATABASE);
	}
	
	private void initDB() {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScript(new ClassPathResource(getPrefixedProperty(INIT_DB)));
		DatabasePopulatorUtils.execute(databasePopulator, dataSource());
	}

	private String getPrefixedProperty(String key) {
		return getPrefixedProperty(key, String.class);
	}
	
	private <T> T getPrefixedProperty(String key, Class<T> targetType) {
		String prefix = activeDatabase();
		return env.getProperty(MessageFormat.format(key, prefix), targetType);
	}

	
}
