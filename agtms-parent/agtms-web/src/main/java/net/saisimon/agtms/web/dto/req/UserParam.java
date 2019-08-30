package net.saisimon.agtms.web.dto.req;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UserParam {
	
	private Long id;
	
	@NotBlank
	private String loginName;
	
	@NotBlank
	private String password;
	
	private String nickname;
	
	@NotBlank
	private String cellphone;
	
	@NotBlank
	private String email;
	
	private String avatar;
	
	private String remark;
	
}
