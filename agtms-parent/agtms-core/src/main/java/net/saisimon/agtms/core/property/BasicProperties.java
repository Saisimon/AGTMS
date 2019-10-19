package net.saisimon.agtms.core.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 基本属性字段
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
@ToString
@ConfigurationProperties(prefix="agtms.basic")
public class BasicProperties {
	
	private String filepath = "/tmp/files";
	
	private String generateClasspath = "/tmp/classes";
	
	private int taskMaxSize = 1024;
	
	private int exportRowsMaxSize = 65535;
	
	private int importRowsMaxSize = 65535;
	
	private int importFileMaxSize = 10;
	
	private int maxDepth = 3;
	
	private List<String> excludeServices = new ArrayList<>(Arrays.asList("agtms-admin", "agtms-web", "agtms-zuul"));
	
	private String pdfFontFamily = "Arial Unicode MS";
	
	private String pdfFontPath;
	
}
