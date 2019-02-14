package net.saisimon.agtms.core.cache;

import org.springframework.core.Ordered;

/**
 * 缓存接口
 * 
 * @author saisimon
 *
 */
public interface Cache extends Ordered {
	
	/**
	 * 获取指定缓存（缓存自动续期）
	 * 
	 * @param key 缓存key
	 * @param valueClass 缓存对象的类型
	 * @param genericClasses 缓存对象包含的泛型类型
	 * @return 缓存对象
	 */
	default <T> T get(String key, Class<T> valueClass, Class<?>... genericClasses) {
		return get(key, true, valueClass, genericClasses);
	}
	
	/**
	 * 获取指定缓存
	 * 
	 * @param key 缓存key
	 * @param update 是否续期
	 * @param valueClass 缓存对象的类型
	 * @param genericClasses 缓存对象包含的泛型类型
	 * @return 缓存对象
	 */
	<T> T get(String key, boolean update, Class<T> valueClass, Class<?>... genericClasses);
	
	/**
	 * 设置指定缓存
	 * 
	 * @param key 缓存key
	 * @param value 缓存对象
	 * @param timeout 过期时间（小于等于 0 代表无过期时间）单位毫秒
	 */
	<T> void set(String key, T value, long timeout);
	
	/**
	 * 删除指定缓存
	 * 
	 * @param key 缓存key
	 */
	void delete(String key);
	
}
