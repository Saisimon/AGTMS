package net.saisimon.agtms.web.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import net.saisimon.agtms.jpa.dialect.Dialect;
import net.saisimon.agtms.jpa.dialect.H2Dialect;

@TestConfiguration
public class TestConfig {

	@Bean
	public Dialect h2Dialect() {
		return new H2Dialect();
	}

}
