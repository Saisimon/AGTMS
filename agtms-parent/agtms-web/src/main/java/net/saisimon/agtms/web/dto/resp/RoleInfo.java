package net.saisimon.agtms.web.dto.resp;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.web.dto.resp.common.BaseInfo;

@Data
@EqualsAndHashCode(callSuper=true)
public class RoleInfo extends BaseInfo {
	
	private static final long serialVersionUID = -4520968094243140459L;
	
	private String name;
	
	private String operator;
	
	private Date createTime;
	
	private Date updateTime;
	
}
