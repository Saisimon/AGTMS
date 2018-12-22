package net.saisimon.agtms.core.enums;

public enum Functions {
	
	CREATE("create"), 
	EDIT("edit"),
	BATCH_EDIT("batchEdit"),
	REMOVE("remove"), 
	BATCH_REMOVE("batchRemove"), 
	EXPORT("export"), 
	IMPORT("import");
	
	private String function;
	
	Functions(String function) {
		this.function = function;
	}
	
	public String getFunction() {
		return function;
	}
	
}
