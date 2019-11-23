package net.saisimon.agtms.web.dto.resp;

import java.io.Serializable;

import lombok.Data;

/**
 * 统计信息对象
 * 
 * @author saisimon
 *
 */
@Data
public class StatisticsInfo implements Serializable {
	
	private static final long serialVersionUID = 5950797093647718629L;

	private Long userCount;
	
	private Long navigationCount;
	
	private Long templateCount;
	
	private Long selectionCount;
	
	private Long taskCount;
	
	private Long notificationCount;
	
}
