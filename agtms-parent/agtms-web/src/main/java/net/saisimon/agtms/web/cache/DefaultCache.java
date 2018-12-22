package net.saisimon.agtms.web.cache;

import org.springframework.stereotype.Component;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import net.saisimon.agtms.core.cache.Cache;
import net.saisimon.agtms.core.order.AbstractOrder;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class DefaultCache extends AbstractOrder implements Cache {
	
	private static final TimedCache<String, String> CACHE = CacheUtil.newTimedCache(0);

	@Override
	public <T> T get(String key, Class<T> valueClass, Class<?>... genericClasses) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		String json = CACHE.get(key);
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
			CACHE.put(key, json, timeout);
		} else {
			CACHE.put(key, json);
		}
	}

	@Override
	public void delete(String key) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		CACHE.remove(key);
	}

}
