package net.saisimon.agtms.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 简单响应对象
 * 
 * @author saisimon
 *
 * @param <T>
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SimpleResult<T> extends Result {
	
	/**
	 * 响应体
	 */
	private T data;
	
}
