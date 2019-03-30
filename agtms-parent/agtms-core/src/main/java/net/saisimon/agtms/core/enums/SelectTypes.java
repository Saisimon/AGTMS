package net.saisimon.agtms.core.enums;

/**
 * 下拉列表内容来源类型枚举
 * 
 * @author saisimon
 *
 */
public enum SelectTypes {
	
	/**
	 * 远程调用类型
	 */
	REMOTE(-1, "remote"),
	/**
	 * 选项类型
	 */
	OPTION(0, "option"),
	/**
	 * 模版类型
	 */
	TEMPLATE(1, "template");
	
	private Integer type;
	private String name;
	
	SelectTypes(Integer type, String name) {
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
