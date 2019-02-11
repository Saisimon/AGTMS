package net.saisimon.agtms.core.enums;

public enum Selections {
	
	CLASS("class"),
	DATA_SOURCE("data_source"),
	FILTER_TYPE("filter_type"),
	FUNCTION("function"),
	NAVIGATION("navigation"),
	HANDLE_STATUS("handle_status"),
	OPERATE_TYPE("operate_type"),
	TASK_TYPE("task_type"),
	FILE_TYPE("file_type"),
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
