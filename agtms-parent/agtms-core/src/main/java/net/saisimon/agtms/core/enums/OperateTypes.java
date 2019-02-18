package net.saisimon.agtms.core.enums;

/**
 * 操作类型枚举
 * 
 * @author saisimon
 *
 */
public enum OperateTypes {
	
	LOGOUT(0, "logout"),
	REGISTER(10, "register"),
	LOGIN(20, "login"),
	QUERY(30, "query"), 
	EDIT(40, "edit"),
	REMOVE(50, "remove"),
	BATCH_EDIT(60, "batch.edit"),
	BATCH_REMOVE(70, "batch.remove"),
	EXPORT(80, "export"),
	IMPORT(90, "import");
	
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
