package net.saisimon.agtms.zuul.config.property;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * 白名单属性对象
 * 
 * @author saisimon
 *
 */
@RefreshScope
@Component
@ConfigurationProperties(prefix="white.list")
@Data
public class WhiteList {
	
	private List<String> urls;
	
}
