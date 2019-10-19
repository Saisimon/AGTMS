package net.saisimon.agtms.web.dto.resp;

import lombok.Data;

/**
 * 个人资料信息对象
 * 
 * @author saisimon
 *
 */
@Data
public class ProfileInfo {
	
	private String loginName;
	
	private String nickname;
	
	private String avatar;
	
	private String remark;
	
	private String email;
	
	private String cellphone;
	
}
