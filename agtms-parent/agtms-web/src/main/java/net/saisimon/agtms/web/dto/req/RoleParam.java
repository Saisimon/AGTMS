package net.saisimon.agtms.web.dto.req;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * 角色参数对象
 * 
 * @author saisimon
 *
 */
@Data
public class RoleParam {
	
	private Long id;
	
	@NotBlank
	private String name;
	
	@NotNull
	private String path;
	
	private List<String> resources;
	
	private String remark;
	
}
