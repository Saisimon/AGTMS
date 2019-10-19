package net.saisimon.agtms.web.dto.resp;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.web.dto.resp.common.BaseInfo;

/**
 * 用户信息对象
 * 
 * @author saisimon
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class UserInfo extends BaseInfo {

	private static final long serialVersionUID = 2264364613100573678L;

	private String loginName;
	
	private String nickname;
	
	private String cellphone;
	
	private String email;
	
	private String avatar;
	
	private String status;
	
	private Date lastLoginTime;
	
	private Date createTime;
	
	private Date updateTime;
	
}
