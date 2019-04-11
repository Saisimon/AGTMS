package net.saisimon.agtms.core.enums;

/**
 * 是否枚举
 * 
 * @author saisimon
 *
 */
public enum Whether {
	
	/**
	 * 否
	 */
	NO(0, "no"),
	/**
	 * 是
	 */
	YES(1, "yes");
	
	private Integer value;
	private String name;
	
	Whether(Integer value, String name) {
		this.value = value;
		this.name = name;
	}
	
	public Integer getValue() {
		return this.value;
	}
	
	public String getName() {
		return this.name;
	}
	
}
