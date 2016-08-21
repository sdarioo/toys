package com.examples.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.examples.demo.config.DataSourceBuilder;
import com.examples.demo.util.SQLExecutionListener;

import net.ttddyy.dsproxy.listener.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

@Configuration
public class TestConfig {

	@Autowired
	private Environment env;
	
	@Bean(name="dataSource")
	public DataSource dataSource()	{
		
		DataSource ds = DataSourceBuilder.buildDataSource(
				env.getProperty("hsql.driverClassName"),  
				env.getProperty("hsql.url"),
				env.getProperty("hsql.username"),
				env.getProperty("hsql.password"));
		
		return ProxyDataSourceBuilder
		        .create(ds)
		        .logQueryBySlf4j(SLF4JLogLevel.INFO)
		        .countQuery()
		        .listener(SQLExecutionListener.getDefault())
		        .build();
	}
}
