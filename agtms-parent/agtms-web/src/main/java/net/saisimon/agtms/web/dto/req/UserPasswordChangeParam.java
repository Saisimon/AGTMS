package net.saisimon.agtms.web.dto.req;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户修改密码参数对象
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
public class UserPasswordChangeParam implements Serializable {

	private static final long serialVersionUID = 5696615388572482761L;
	
	@NotBlank
	private String oldPassword;
	
	@NotBlank
	private String newPassword;
	
}
