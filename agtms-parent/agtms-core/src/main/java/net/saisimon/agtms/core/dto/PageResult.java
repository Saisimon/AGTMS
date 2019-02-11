package net.saisimon.agtms.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class PageResult<S> extends Result {
	
	private Long total;
	
	private Iterable<S> rows;
	
}
