package com.examples.demo;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import net.ttddyy.dsproxy.listener.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;


@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public DataSource dataSource() {
        return ProxyDataSourceBuilder
                .create(actualDataSource())
                .name("MyDS")
                .logQueryBySlf4j(SLF4JLogLevel.INFO)
                .build();
    }

    @Bean
    CommandLineRunner init(EntityManager em, PlatformTransactionManager txManager) {  
        return args -> {
        };
    }
    
    
    private DataSource actualDataSource() {
        return DataSourceBuilder
                .create(Application.class.getClassLoader())
                .driverClassName("org.hsqldb.jdbcDriver")
                .url("jdbc:hsqldb:mem:testdb")  // in-memory db
                .username("sa")
                .password("")
                .build();
    }
    

}
