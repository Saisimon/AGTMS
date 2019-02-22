package net.saisimon.agtms.redis.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.cache.AbstractCache;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.redis.order.RedisOrder;

@Component
public class RedisCache extends AbstractCache implements RedisOrder {
	
	private Map<String, Long> timeoutMap = new ConcurrentHashMap<>();
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Override
	protected String get(String key, boolean update) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		String json = redisTemplate.opsForValue().get(key);
		if (update && json != null) {
			Long timeout = timeoutMap.getOrDefault(key, 0L);
			set(key, json, timeout);
		}
		return json;
	}
	
	@Override
	protected void set(String key, String value, long timeout) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		if (timeout > 0) {
			redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
		} else {
			redisTemplate.opsForValue().set(key, value);
		}
		timeoutMap.put(key, timeout);
	}
	
	@Override
	public void delete(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		redisTemplate.delete(key);
		timeoutMap.remove(key);
	}

}
