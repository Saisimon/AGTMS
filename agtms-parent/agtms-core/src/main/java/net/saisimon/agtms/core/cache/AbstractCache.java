package net.saisimon.agtms.core.cache;

import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 基础的缓存抽象类
 * 
 * @author saisimon
 *
 */
public abstract class AbstractCache implements Cache {

	@Override
	public <T> T get(String key, boolean update, Class<T> valueClass, Class<?>... genericClasses) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		String json = get(key, update);
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
		set(key, json, timeout);
	}
	
	protected abstract String get(String key, boolean update);
	
	protected abstract void set(String key, String value, long timeout);
	
}
