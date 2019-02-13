package net.saisimon.agtms.web.cache;

import org.springframework.stereotype.Component;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import net.saisimon.agtms.core.cache.AbstractCache;
import net.saisimon.agtms.core.order.BaseOrder;
import net.saisimon.agtms.core.util.StringUtils;

/**
 * 默认本地缓存
 * 
 * @author saisimon
 *
 */
@Component
public class DefaultCache extends AbstractCache implements BaseOrder {
	
	private static final TimedCache<String, String> CACHE = CacheUtil.newTimedCache(0);
	
	@Override
	protected String get(String key, boolean update) {
		return CACHE.get(key, update);
	}
	
	@Override
	protected void set(String key, String value, long timeout) {
		if (timeout > 0) {
			CACHE.put(key, value, timeout);
		} else {
			CACHE.put(key, value);
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
