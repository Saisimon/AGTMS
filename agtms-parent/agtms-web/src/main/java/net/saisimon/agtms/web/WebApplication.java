package net.saisimon.agtms.web;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringCloudApplication
@ComponentScan(basePackages="net.saisimon.agtms")
public class WebApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);
	}
	
}
