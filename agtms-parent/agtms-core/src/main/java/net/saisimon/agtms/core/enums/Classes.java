package net.saisimon.agtms.core.enums;

/**
 * 模版列属性类型枚举
 * 
 * @author saisimon
 *
 */
public enum Classes {
	
	STRING("string"), 
	LONG("long"), 
	DOUBLE("double"), 
	DATE("date");
	
	private String name;
	
	Classes(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
}
