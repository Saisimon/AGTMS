package net.saisimon.agtms.core.enums;

public enum Selections {
	
	CLASS("class"),
	DATA_SOURCE("data_source"),
	FILTER_TYPE("filter_type"),
	FUNCTION("function"),
	NAVIGATE("navigate"),
	SEX("sex"),
	VIEW("view"),
	WHETHER("whether");
	
	private String name;
	
	Selections(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
}
