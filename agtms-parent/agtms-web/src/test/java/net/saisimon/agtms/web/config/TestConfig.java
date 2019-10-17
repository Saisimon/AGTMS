package net.saisimon.agtms.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer;
import org.springframework.web.context.WebApplicationContext;

import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.property.AccountProperties;
import net.saisimon.agtms.core.property.BasicProperties;
import net.saisimon.agtms.core.property.EncryptorProperties;

@TestConfiguration
public class TestConfig {
	
	@Autowired
	private WebApplicationContext wac;
	
	@Bean
	public MockMvc mockMvc() {
		return MockMvcBuilders.webAppContextSetup(wac).apply(SharedHttpSessionConfigurer.sharedHttpSession()).build();
	}
	
	@Bean
	public BasicProperties basicProperties() {
		return new BasicProperties();
	}
	
	@Bean
	public AccountProperties accountProperties() {
		return new AccountProperties();
	}
	
	@Bean
	public EncryptorProperties encryptorProperties() {
		return new EncryptorProperties();
	}
	
	@Bean
	public DomainGenerater domainGenerater() {
		return new DomainGenerater(basicProperties());
	}
	
}