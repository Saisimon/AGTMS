package net.saisimon.agtms.core.enums;

/**
 * 用户状态枚举
 * 
 * @author saisimon
 *
 */
public enum UserStatuses {
	
	/**
	 * 锁定
	 */
	LOCKED(-10, "locked"),
	/**
	 * 正常
	 */
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
