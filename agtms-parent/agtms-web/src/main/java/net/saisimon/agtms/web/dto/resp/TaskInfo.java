package net.saisimon.agtms.web.dto.resp;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.web.dto.resp.common.BaseInfo;

/**
 * 任务信息对象
 * 
 * @author saisimon
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class TaskInfo extends BaseInfo {

	private static final long serialVersionUID = 2169109875986378350L;
	
	private String uuid;
	
	private String taskContent;

	private String taskType;
	
	private Date taskTime;
	
	private String operator;
	
	private Date handleTime;
	
	private String handleStatus;
	
	private String handleResult;
	
}
