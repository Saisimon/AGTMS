package net.saisimon.agtms.web.dto.resp;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.web.dto.resp.common.BaseInfo;

@Data
@EqualsAndHashCode(callSuper=true)
public class UserInfo extends BaseInfo {

	private static final long serialVersionUID = 2264364613100573678L;

	private String loginName;
	
	private String nickName;
	
	private String cellphone;
	
	private String email;
	
	private Date createTime;
	
	private Date updateTime;
	
	private Date lastLoginTime;
	
}
