package net.saisimon.agtms.web.dto.resp;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.web.dto.resp.common.BaseInfo;

@Data
@EqualsAndHashCode(callSuper=true)
public class NavigationInfo extends BaseInfo {
	
	private static final long serialVersionUID = 2294019883615427021L;

	private String title;
	
	private String icon;
	
	private Long priority;
	
	private String parentId;
	
	private String operator;
	
	private Date createTime;
	
	private Date updateTime;
	
}
