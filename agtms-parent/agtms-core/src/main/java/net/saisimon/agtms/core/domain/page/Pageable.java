package net.saisimon.agtms.core.domain.page;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Pageable {
	
	private Integer pageIndex;
	
	private Integer pageSize;
	
}
