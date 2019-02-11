package net.saisimon.agtms.core.enums;

public enum EditorTypes {
	
	INPUT("input"), 
	SELECT("select"),
	REMOVE("remove"), 
	TABLE("table");
	
	private String type;
	
	EditorTypes(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}
