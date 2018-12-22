package net.saisimon.agtms.core.enums;

public enum Classes {
	
	INTEGER("java.lang.Integer"), DATE("date"), STRING("java.lang.String"), DOUBLE("java.lang.Double");
	
	private String clazz;
	
	Classes(String clazz) {
		this.clazz = clazz;
	}
	
	public String getClazz() {
		return this.clazz;
	}
	
}
