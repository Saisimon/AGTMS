package net.saisimon.agtms.core.enums;

/**
 * 消息通知类型枚举
 * 
 * @author saisimon
 *
 */
public enum NotificationTypes {

	/**
	 * 系统通知
	 */
	SYSTEM_NOTICE(0, "system.notification"),
	/**
	 * 任务通知
	 */
	TASK_NOTICE(1, "task.notification");
	
	private Integer type;
	private String name;
	
	NotificationTypes(Integer type, String name) {
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
