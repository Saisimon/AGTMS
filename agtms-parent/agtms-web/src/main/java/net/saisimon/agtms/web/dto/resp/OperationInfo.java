package net.saisimon.agtms.web.dto.resp;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.web.dto.resp.common.BaseInfo;

@Data
@EqualsAndHashCode(callSuper=true)
public class OperationInfo extends BaseInfo {

	private static final long serialVersionUID = 2169109875986378350L;

	private String operateType;
	
	private Date operateTime;
	
	private String operateIp;
	
	private String operateContent;
	
	private String operator;
	
}
