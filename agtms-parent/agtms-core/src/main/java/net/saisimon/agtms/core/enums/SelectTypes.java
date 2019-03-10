package net.saisimon.agtms.core.enums;

public enum SelectTypes {
	
	OPTION(0, "option"),
	TEMPLATE(1, "template");
	
	private Integer type;
	private String name;
	
	SelectTypes(Integer type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getType() {
		return type;
	}
	
}
