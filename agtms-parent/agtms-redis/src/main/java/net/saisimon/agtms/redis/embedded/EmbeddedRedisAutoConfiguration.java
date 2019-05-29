package net.saisimon.agtms.redis.embedded;

import java.net.UnknownHostException;
import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.util.NetUtil;
import net.saisimon.agtms.core.util.SystemUtils;
import redis.embedded.RedisServer;

/**
 * 嵌入式 Redis 自动配置类
 * 
 * @author saisimon
 *
 */
@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
@ConditionalOnClass({RedisServer.class, LettuceConnectionFactory.class})
@ConditionalOnProperty(name = "spring.redis.embedded.enabled", havingValue = "true", matchIfMissing = true)
public class EmbeddedRedisAutoConfiguration {
	
	@Bean(initMethod="start", destroyMethod="stop")
	public RedisServer embeddedRedisServer() {
		int port = SystemUtils.getAvailableLocalPort();
		if (port == -1) {
			throw new IllegalArgumentException("嵌入式 Redis 服务没有可用的端口");
		}
		return new RedisServer(port);
	}
	
	@Bean
	@ConditionalOnBean(RedisServer.class)
	public RedisConnectionFactory redisConnectionFactory(RedisServer redisServer) throws UnknownHostException {
		List<Integer> ports = redisServer.ports();
		if (CollectionUtils.isEmpty(ports)) {
			throw new IllegalArgumentException("获取嵌入式 Redis 服务端口失败");
		}
		return new LettuceConnectionFactory(NetUtil.LOCAL_IP, ports.get(0));
	}
	
}
