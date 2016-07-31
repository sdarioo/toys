package com.examples.demo;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.examples.demo.config.AppConfig;

@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(Profiles.TEST_PROFILE)
@TestPropertySource(properties={"active.database=hsql"})
public abstract class IntegrationTestBase {
	
	protected IntegrationTestBase() {}

}
