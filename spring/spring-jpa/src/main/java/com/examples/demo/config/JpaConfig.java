package com.examples.demo.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.examples.demo.App;

import net.ttddyy.dsproxy.listener.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("com.examples.demo.repository")
@PropertySource("classpath:data-access.properties")
public class JpaConfig {
	
	@Autowired
	private Environment env;
	
	private static final String PERSISTENCE_UNIT_NAME = "demoPersistenceUnit";
	
	@PostConstruct
	private void postConstruct()
	{
		String profile = activeProfile();
		if ((profile == null) || !Arrays.asList(App.DEV_PROFILE, App.TEST_PROFILE).contains(profile)) {
			throw new RuntimeException("Invalid active profile: " + profile);
		}
	}
	
	@Bean(name="dataSource", destroyMethod="close")
	public DataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(getPrefixedProperty("driverClassName"));
        ds.setUrl(getPrefixedProperty("url"));
        ds.setUsername(getPrefixedProperty("username"));
        ds.setPassword(getPrefixedProperty("password"));
        
		ds.setMaxTotal(4);
		ds.setMinIdle(1);
		ds.setMaxIdle(2);
		ds.setMaxWaitMillis(100);
        
		return ds;
	}
	
	@Bean(name="proxyDataSource")
	public DataSource proxyDataSource()
	{
		DataSource ds = dataSource();
		return ProxyDataSourceBuilder
		        .create(ds)
		        .logQueryBySlf4j(SLF4JLogLevel.INFO)
		        .countQuery()
		        .build();
	}
	
	@Bean(name="entityManagerFactory")
	public AbstractEntityManagerFactoryBean entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(getPrefixedProperty("database", Database.class));
		
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
		emf.setDataSource(isUseProxyDataSource() ? proxyDataSource() : dataSource());
		emf.setJpaVendorAdapter(vendorAdapter);
		return emf;
	}
	
	@Bean(name="transactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(emf);
        return jpaTransactionManager;
	}

	private String activeProfile()
	{
		String[] activeProfiles = env.getActiveProfiles();
		if ((activeProfiles != null) && (activeProfiles.length > 0)) {
			return activeProfiles[0];
		}
		return null;
	}

	private boolean isUseProxyDataSource()
	{
		return env.getProperty("data.source.proxy", Boolean.class);
	}
	
	private String getPrefixedProperty(String key)
	{
		return getPrefixedProperty(key, String.class);
	}
	
	private <T> T getPrefixedProperty(String key, Class<T> targetType)
	{
		return env.getProperty(activeProfile() + '.' + key, targetType);
	}
	
	
}
