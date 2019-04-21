package net.saisimon.agtms.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * 响应对象
 * 
 * @author saisimon
 *
 */
@Data
@JsonInclude(Include.NON_NULL)
public class Result {
	
	/**
	 * 响应代码
	 */
	private Integer code;
	
	/**
	 * 响应消息
	 */
	private String message;
	
	/**
	 * 响应消息参数
	 */
	private Object[] messageArgs;
	
	public Result messageArgs(Object... messageArgs) {
		this.messageArgs = messageArgs;
		return this;
	}
	
}
