package net.saisimon.agtms.core.enums;

public enum FileTypes {
	
	XLS("xls"),
	XLSX("xlsx"),
	CSV("csv"),
	JSON("json");
	
	private String type;
	
	FileTypes(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}
