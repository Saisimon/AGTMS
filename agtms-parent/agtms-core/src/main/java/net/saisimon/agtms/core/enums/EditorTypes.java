package net.saisimon.agtms.core.enums;

/**
 * 
 * 
 * @author saisimon
 *
 */
public enum EditorTypes {
	
	/**
	 * 输入框类型
	 */
	INPUT("input"), 
	/**
	 * 下拉列表类型
	 */
	SELECT("select"),
	/**
	 * 删除类型
	 */
	REMOVE("remove"), 
	/**
	 * 表格类型
	 */
	TABLE("table");
	
	private String type;
	
	EditorTypes(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
}
