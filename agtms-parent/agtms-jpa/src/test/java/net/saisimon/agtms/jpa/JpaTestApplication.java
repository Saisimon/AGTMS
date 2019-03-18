package net.saisimon.agtms.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="net.saisimon.agtms")
public class JpaTestApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(JpaTestApplication.class, args);
	}
	
}
