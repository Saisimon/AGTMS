package net.saisimon.agtms.mongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="net.saisimon.agtms")
public class MongodbTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongodbTestApplication.class, args);
	}
	
}
