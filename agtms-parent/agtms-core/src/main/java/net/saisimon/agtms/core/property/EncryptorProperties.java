package net.saisimon.agtms.core.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@ConfigurationProperties(prefix="agtms.encryptor")
public class EncryptorProperties {
	
	private String algorithm = "aes";
	
	private String secret = "agtms";
	
}
