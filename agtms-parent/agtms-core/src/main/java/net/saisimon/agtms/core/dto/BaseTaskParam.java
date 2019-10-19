package net.saisimon.agtms.core.dto;

import lombok.Data;

/**
 * 基础任务参数对象
 * 
 * @author saisimon
 *
 */
@Data
public abstract class BaseTaskParam {

	private String uuid;
	
	private Long userId;
	
	private Object templateId;
	
}
