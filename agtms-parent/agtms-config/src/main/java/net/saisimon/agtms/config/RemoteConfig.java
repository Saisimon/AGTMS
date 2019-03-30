package net.saisimon.agtms.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.saisimon.agtms.core.service.RemoteService;
import net.saisimon.agtms.remote.service.RemoteApiService;

/**
 * 远程调用配置类
 * 
 * @author saisimon
 *
 */
@Configuration
@ConditionalOnClass(RemoteApiService.class)
public class RemoteConfig {
	
	/**
	 * 远程服务接口
	 * 
	 * @return 远程服务接口
	 */
	@Bean
	public RemoteService remoteService() {
		return new RemoteApiService();
	}
	
}
