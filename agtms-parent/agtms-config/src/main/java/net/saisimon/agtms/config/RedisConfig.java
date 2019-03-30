package net.saisimon.agtms.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import net.saisimon.agtms.redis.order.RedisOrder;

/**
 * Redis 配置类
 * 
 * @author saisimon
 *
 */
@Configuration
@ConditionalOnClass({RedisOrder.class, Hibernate5Module.class})
public class RedisConfig {
	
	/**
	 * 缓存最大存活时间
	 * 单位：秒
	 */
	@Value("${extra.max-size.cache.ttl:600}")
	private int cacheTtl;

	/**
	 * Redis 缓存管理器
	 * 
	 * @param connectionFactory Redis 链接工厂
	 * @return Redis 缓存管理器
	 */
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);
		Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		ObjectMapper objectMapper = new ObjectMapper();
		// 解决 Hibernate 懒加载序列化问题
		objectMapper.registerModule(new Hibernate5Module().enable(Hibernate5Module.Feature.REPLACE_PERSISTENT_COLLECTIONS));
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
		SerializationPair<?> pair = RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer);
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(cacheTtl)).serializeValuesWith(pair);
		return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
	}
	
}
