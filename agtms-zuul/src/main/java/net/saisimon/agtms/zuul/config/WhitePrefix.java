package net.saisimon.agtms.zuul.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Data;

@RefreshScope
@Component
@ConfigurationProperties(prefix="white.prefix")
@Data
public class WhitePrefix {
	
	private List<String> urls;
	
}
