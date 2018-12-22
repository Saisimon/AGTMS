package net.saisimon.agtms.redis.cache;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.cache.Cache;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.redis.order.AbstractRedisOrder;

@Component
public class RedisCache extends AbstractRedisOrder implements Cache {
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Override
	public <T> T get(String key, Class<T> valueClass, Class<?>... genericClasses) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		String json = redisTemplate.opsForValue().get(key);
		if (StringUtils.isBlank(json)) {
			return null;
		}
		return SystemUtils.fromJson(json, valueClass, genericClasses);
	}
	
	@Override
	public <T> void set(String key, T value, long timeout) {
		if (StringUtils.isBlank(key) || value == null) {
			return;
		}
		String json = SystemUtils.toJson(value);
		if (timeout > 0) {
			redisTemplate.opsForValue().set(key, json, timeout, TimeUnit.MILLISECONDS);
		} else {
			redisTemplate.opsForValue().set(key, json);
		}
	}
	
	@Override
	public void delete(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		redisTemplate.delete(key);
	}
	
}
