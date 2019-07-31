package net.saisimon.agtms.core.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString(exclude = { "adminPassword", "resetPassword" })
@ConfigurationProperties(prefix="agtms")
public class AgtmsProperties {
	
	private String filepath = "/tmp/files";
	
	private String generateClasspath = "/tmp/classes";
	
	private String adminUsername = "admin";
	
	private String adminPassword = "123456";
	
	private String resetPassword = "123456";
	
	private int taskMaxSize = 1024;
	
	private int exportRowsMaxSize = 65535;
	
	private int importRowsMaxSize = 65535;
	
	private int importFileMaxSize = 10;
	
	private List<String> excludeServices = new ArrayList<>(Arrays.asList("agtms-admin", "agtms-web", "agtms-zuul"));
	
}
