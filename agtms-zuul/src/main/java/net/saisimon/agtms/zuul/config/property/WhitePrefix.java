package net.saisimon.agtms.zuul.config.property;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 白名单前缀属性对象
 * 
 * @author saisimon
 *
 */
@RefreshScope
@Component
@ConfigurationProperties(prefix="white.prefix")
@Data
public class WhitePrefix {
	
	private List<String> urls;
	
}
