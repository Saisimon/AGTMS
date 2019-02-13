package net.saisimon.agtms.core.domain;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.generate.DomainGenerater;

/**
 * 自定义对象接口
 * 
 * @author saisimon
 *
 */
public interface Domain {
	
	/**
	 * 获取指定属性值
	 * 
	 * @param fieldName 属性名称
	 * @return 属性值
	 */
	default Object getField(String fieldName) {
		return DomainGenerater.getField(this, fieldName);
	}

	/**
	 * 设置指定属性的属性值
	 * 
	 * @param fieldName 属性名称
	 * @param fieldValue 属性值
	 * @param fieldClass 属性值的类型
	 */
	default void setField(String fieldName, Object fieldValue, Class<?> fieldClass) {
		DomainGenerater.setField(this, fieldName, fieldValue, fieldClass);
	}
	
	default Map<String, Object> toMap(Map<String, String> mapping) {
		if (!CollectionUtils.isEmpty(mapping)) {
			if (!mapping.containsKey(Constant.ID)) {
				mapping.put(Constant.ID, Constant.ID);
			}
			Map<String, Object> map = new HashMap<>();
			for (Entry<String, String> entry : mapping.entrySet()) {
				Field field = ReflectionUtils.findField(this.getClass(), entry.getKey());
				if (field != null) {
					field.setAccessible(true);
					map.put(entry.getValue(), ReflectionUtils.getField(field, this));
				}
			}
			return map;
		} else {
			return null;
		}
	}
	
}
