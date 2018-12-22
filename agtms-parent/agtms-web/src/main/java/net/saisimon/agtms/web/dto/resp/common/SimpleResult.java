package net.saisimon.agtms.web.dto.resp.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class SimpleResult<T> extends Result {
	
	private T data;
	
}
