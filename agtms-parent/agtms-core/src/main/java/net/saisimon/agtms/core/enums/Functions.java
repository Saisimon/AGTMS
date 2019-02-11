package net.saisimon.agtms.core.enums;

public enum Functions {
	
	CREATE(1, "create"), 				// 0000001
	EDIT(2, "edit"), 					// 0000010
	BATCH_EDIT(4, "batchEdit"), 		// 0000100
	REMOVE(8, "remove"), 				// 0001000
	BATCH_REMOVE(16, "batchRemove"), 	// 0010000
	EXPORT(32, "export"), 				// 0100000
	IMPORT(64, "import"); 				// 1000000
	
	private Integer code;
	private String function;
	
	Functions(Integer code, String function) {
		this.code = code;
		this.function = function;
	}
	
	public String getFunction() {
		return function;
	}
	
	public Integer getCode() {
		return code;
	}
	
}
