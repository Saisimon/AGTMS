package net.saisimon.agtms.web.dto.req;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude="password")
public class UserAuthParam implements Serializable {

	private static final long serialVersionUID = 2778584471636207348L;

	@NotBlank
	private String name;
	
	@NotBlank
	private String password;
	
}
