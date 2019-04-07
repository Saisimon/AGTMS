package net.saisimon.agtms.core.enums;

/**
 * 用户状态枚举
 * 
 * @author saisimon
 *
 */
public enum UserStatuses {
	
	LOCKED(-10, "locked"),
	NORMAL(0, "normal");
	
	/**
	 * 状态值
	 */
	private Integer status;
	/**
	 * 状态名称
	 */
	private String name;
	
	UserStatuses(Integer status, String name) {
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
