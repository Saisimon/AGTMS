package net.saisimon.agtms.web.dto.resp;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.web.dto.resp.common.BaseInfo;

@Data
@EqualsAndHashCode(callSuper=true)
public class SelectionInfo extends BaseInfo {

	private static final long serialVersionUID = 2169109875986378350L;
	
	private String title;
	
	private String type;
	
	private String operator;

	private Date createTime;
	
	private Date updateTime;
	
}
