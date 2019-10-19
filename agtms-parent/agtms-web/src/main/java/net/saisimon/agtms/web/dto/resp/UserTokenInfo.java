package net.saisimon.agtms.web.dto.resp;

import lombok.Data;

/**
 * 用户令牌信息对象
 * 
 * @author saisimon
 *
 */
@Data
public class UserTokenInfo {
	
	private String userId;
	
	private String token;
	
	private Long expireTime;
	
	private Integer status;
	
	private String loginName;
	
	private String avatar;
	
}
