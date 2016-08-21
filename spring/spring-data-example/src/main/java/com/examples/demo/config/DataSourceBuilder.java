package com.examples.demo.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

public final class DataSourceBuilder 
{
	private DataSourceBuilder() {}
	
	public static DataSource buildDataSource(String driver, String url, String user, String pass) {
		
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        
		ds.setMaxTotal(4);
		ds.setMinIdle(1);
		ds.setMaxIdle(2);
		ds.setMaxWaitMillis(100);
        
		return ds;
	}
}
