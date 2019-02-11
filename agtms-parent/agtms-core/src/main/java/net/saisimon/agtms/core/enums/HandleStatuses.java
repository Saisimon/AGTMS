package net.saisimon.agtms.core.enums;

public enum HandleStatuses {
	
	CREATE(10, "create"), 
	PROCESSING(20, "processing"),
	SUCCESS(30, "success"),
	FAILURE(40, "failure"),
	CANCEL(50, "cancel");
	
	private Integer status;
	private String name;
	
	HandleStatuses(Integer status, String name) {
		this.status = status;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getStatus() {
		return status;
	}
	
}
