package net.saisimon.agtms.core.cache;

import org.springframework.core.Ordered;

public interface Cache extends Ordered {
	
	default <T> T get(String key, Class<T> valueClass, Class<?>... genericClasses) {
		return get(key, true, valueClass, genericClasses);
	}
	
	<T> T get(String key, boolean update, Class<T> valueClass, Class<?>... genericClasses);
	
	<T> void set(String key, T value, long timeout);
	
	void delete(String key);
	
}
