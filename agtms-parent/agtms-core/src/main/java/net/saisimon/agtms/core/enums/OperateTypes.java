package net.saisimon.agtms.core.enums;

public enum OperateTypes {
	
	CREATE(10, "create"), 
	EDIT(20, "edit"),
	REMOVE(30, "remove"),
	EXPORT(40, "export"),
	IMPORT(50, "import");
	
	private Integer type;
	private String name;
	
	OperateTypes(Integer type, String name) {
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
