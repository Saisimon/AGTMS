package net.saisimon.agtms.core.enums;

/**
 * 模版列属性类型枚举
 * 
 * @author saisimon
 *
 */
public enum Classes {
	
	LONG("long"), DATE("date"), STRING("string"), DOUBLE("double");
	
	private String name;
	
	Classes(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
}
