package net.saisimon.agtms.core.enums;

/**
 * 任务处理状态枚举
 * 
 * @author saisimon
 *
 */
public enum HandleStatuses {
	
	/**
	 * 创建
	 */
	CREATE(10, "create"), 
	/**
	 * 处理中
	 */
	PROCESSING(20, "processing"),
	/**
	 * 成功
	 */
	SUCCESS(30, "success"),
	/**
	 * 失败
	 */
	FAILURE(40, "failure"),
	/**
	 * 取消
	 */
	CANCEL(50, "cancel");
	
	/**
	 * 状态值
	 */
	private Integer status;
	/**
	 * 状态名称
	 */
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
