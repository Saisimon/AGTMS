package net.saisimon.agtms.autotest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import net.saisimon.agtms.autotest.ChromeTest.TestConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes=TestConfig.class)
@TestPropertySource(locations="classpath:autotest.properties")
public class ChromeTest {
	
	@Autowired
	private AutoTest autoTest;
	
	@Test
	public void test() throws Exception {
		autoTest.autoTest();
	}
	
	@Configuration
	public static class TestConfig {
		
		@Bean
		public AutoTest autoTest() {
			return new ChromeAutoTest();
		}
		
	}
	
}
