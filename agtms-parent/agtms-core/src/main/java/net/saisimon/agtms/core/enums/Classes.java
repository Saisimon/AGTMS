package net.saisimon.agtms.core.enums;

public enum Classes {
	
	INTEGER("int"), DATE("date"), STRING("string"), DOUBLE("double");
	
	private String name;
	
	Classes(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
}
