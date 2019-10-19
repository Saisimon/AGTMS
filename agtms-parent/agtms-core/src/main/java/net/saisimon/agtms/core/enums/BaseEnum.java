package net.saisimon.agtms.core.enums;

/**
 * 基础枚举接口
 * 
 * @author saisimon
 *
 * @param <K> 枚举唯一值类型
 */
public interface BaseEnum<K> {
	
	/**
	 * @return 枚举唯一值
	 */
	K getKey();
	
}
