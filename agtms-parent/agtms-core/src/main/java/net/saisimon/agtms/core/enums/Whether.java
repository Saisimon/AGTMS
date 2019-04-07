package net.saisimon.agtms.core.enums;

public enum Whether {
	
	NO(0, "no"),
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
