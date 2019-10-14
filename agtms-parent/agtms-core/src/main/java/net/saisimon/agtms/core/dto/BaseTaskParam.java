package net.saisimon.agtms.core.dto;

import lombok.Data;

@Data
public abstract class BaseTaskParam {

	private String uuid;
	
	private Long userId;
	
	private Object templateId;
	
}
