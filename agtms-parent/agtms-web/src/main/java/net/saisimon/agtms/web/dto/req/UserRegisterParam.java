package net.saisimon.agtms.web.dto.req;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude="password")
public class UserRegisterParam implements Serializable {

	private static final long serialVersionUID = 4579084334502565067L;

	@NotBlank
	private String name;
	
	@NotBlank
	private String email;
	
	@NotBlank
	private String password;
	
}
