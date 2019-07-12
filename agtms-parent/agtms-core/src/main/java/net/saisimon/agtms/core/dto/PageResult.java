package net.saisimon.agtms.core.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页响应体
 * 
 * @author saisimon
 *
 * @param <S> 响应体类型
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class PageResult<S> extends Result {
	
	/**
	 * 总数
	 */
	private Boolean more;
	
	/**
	 * 列表集合
	 */
	private Iterable<S> rows;
	
}
