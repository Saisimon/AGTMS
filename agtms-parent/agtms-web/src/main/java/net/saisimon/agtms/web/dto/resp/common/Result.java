package net.saisimon.agtms.web.dto.resp.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class Result {
	
	private Integer code;
	
	private String message;
	
	private Object[] messageArgs;
	
}
