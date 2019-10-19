package net.saisimon.agtms.core.domain.grid;

import lombok.Builder;
import lombok.Data;

/**
 * 面包屑对象
 * 
 * @author saisimon
 *
 */
@Data
@Builder
public class Breadcrumb {
	
	private String to;
	
	private boolean active;
	
	private String text;
	
}
