package net.saisimon.agtms.web.dto.req;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class NavigationParam {
	
	private Long id;
	
	@NotBlank
	private String title;
	
	@NotBlank
	private String icon;
	
	private Integer priority;
	
	@NotNull
	private Long parentId;
	
}
