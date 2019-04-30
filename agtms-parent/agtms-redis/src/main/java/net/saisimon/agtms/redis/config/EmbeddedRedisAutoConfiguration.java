package net.saisimon.agtms.redis.config;

import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import cn.hutool.core.util.NetUtil;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class EmbeddedRedisAutoConfiguration {
	
	private final RedisServer redisServer;
	private final int port;
	
	public EmbeddedRedisAutoConfiguration() {
		this.port = SystemUtils.getAvailableLocalPort();
		this.redisServer = new RedisServer(port);
	}
	
	@PostConstruct
	public void start() {
		if (this.redisServer != null) {
			log.info("Start Embedded Redis Server, Port: " + port);
			this.redisServer.start();
		}
	}
	
	@PreDestroy
	public void stop() {
		if (this.redisServer != null) {
			log.info("Stop Embedded Redis Server.");
			this.redisServer.stop();
		}
	}
	
	@Bean
	public RedisServer embeddedRedisServer() {
		return redisServer;
	}
	
	@Bean
	public RedisConnectionFactory redisConnectionFactory() throws UnknownHostException {
		return new LettuceConnectionFactory(NetUtil.LOCAL_IP, port);
	}
	
}
