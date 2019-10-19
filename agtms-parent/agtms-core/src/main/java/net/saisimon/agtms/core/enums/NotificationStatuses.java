package net.saisimon.agtms.core.enums;

/**
 * 消息通知状态枚举
 * 
 * @author saisimon
 *
 */
public enum NotificationStatuses {

	/**
	 * 未读
	 */
	UNREAD(0, "unread"),
	/**
	 * 已读
	 */
	READ(10, "read");
	
	private Integer status;
	private String name;
	
	NotificationStatuses(Integer status, String name) {
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
