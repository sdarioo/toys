package com.examples.demo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({JpaConfig.class})
@ComponentScan("com.examples.demo.service")
public class AppConfig {

}
