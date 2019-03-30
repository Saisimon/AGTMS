package net.saisimon.agtms.core.enums;

/**
 * 模版列属性类型枚举
 * 
 * @author saisimon
 *
 */
public enum Classes {
	
	/**
	 * 字符串
	 */
	STRING("string"), 
	/**
	 * 整数
	 */
	LONG("long"), 
	/**
	 * 小数
	 */
	DOUBLE("double"), 
	/**
	 * 日期
	 */
	DATE("date");
	
	private String name;
	
	Classes(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
}
