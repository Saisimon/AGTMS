package net.saisimon.agtms.web.dto.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class NavigationParam {
	
	private Long id;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String icon;
	
	@NotNull
	private String path;
	
}
