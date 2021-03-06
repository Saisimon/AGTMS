package net.saisimon.agtms.core.domain.filter;

import lombok.Getter;
import lombok.Setter;

/**
 * 前端属性过滤条件对象
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
public class FieldFilter implements Cloneable {
	
	private String type;
	
	public FieldFilter(String type) {
		this.type = type;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
