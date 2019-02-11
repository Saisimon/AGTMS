package net.saisimon.agtms.web.dto.resp;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.web.dto.resp.common.BaseInfo;

@Data
@EqualsAndHashCode(callSuper=true)
public class NavigationInfo extends BaseInfo {
	
	private static final long serialVersionUID = 2294019883615427021L;

	private Long id;
	
	private String title;
	
	private String icon;
	
	private Integer priority;
	
	private Long parentId;
	
	private Date createTime;
	
	private Date updateTime;
	
}
