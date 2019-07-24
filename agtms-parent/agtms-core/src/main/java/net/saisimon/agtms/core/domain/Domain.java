package net.saisimon.agtms.core.domain;

import net.saisimon.agtms.core.util.DomainUtils;

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
		return DomainUtils.getField(this, fieldName);
	}

	/**
	 * 设置指定属性的属性值
	 * 
	 * @param fieldName 属性名称
	 * @param fieldValue 属性值
	 * @param fieldClass 属性值的类型
	 */
	default void setField(String fieldName, Object fieldValue, Class<?> fieldClass) {
		DomainUtils.setField(this, fieldName, fieldValue, fieldClass);
	}
	
}
