package net.saisimon.agtms.web.dto.req;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class NavigationParam {
	
	private Long id;
	
	@NotBlank
	private String name;
	
	@NotBlank
	private String icon;
	
	private String path;
	
}
