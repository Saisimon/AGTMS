package net.saisimon.agtms.core.property;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.saisimon.agtms.core.service.ObjectStorageService;

@Setter
@Getter
@ToString
@ConfigurationProperties(prefix="agtms.oss")
public class OssProperties {
	
	private String type = ObjectStorageService.DEFAULT_TYPE;
	
	private Map<String, String> properties = new HashMap<>();
	
}
