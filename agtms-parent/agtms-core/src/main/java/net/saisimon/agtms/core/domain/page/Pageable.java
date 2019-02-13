package net.saisimon.agtms.core.domain.page;

import lombok.Builder;
import lombok.Data;

/**
 * 页面信息对象
 * 
 * @author saisimon
 *
 */
@Data
@Builder
public class Pageable {
	
	private Integer pageIndex;
	
	private Integer pageSize;
	
}
