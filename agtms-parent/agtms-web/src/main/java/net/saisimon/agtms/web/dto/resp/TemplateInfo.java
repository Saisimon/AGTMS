package net.saisimon.agtms.web.dto.resp;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.web.dto.resp.common.BaseInfo;

@Data
@EqualsAndHashCode(callSuper=true)
public class TemplateInfo extends BaseInfo {
	
	private static final long serialVersionUID = 9049467055032392146L;

	private String navigationName;
	
	private String title;
	
	private String operator;
	
	private Date createTime;
	
	private Date updateTime;
	
}
