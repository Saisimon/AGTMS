package net.saisimon.agtms.web.dto.req;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class NavigationBatchParam {
	
	@NotEmpty
	private List<Long> ids;
	
	private String icon;
	
	private Integer priority;
	
}
