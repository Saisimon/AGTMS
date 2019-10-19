package net.saisimon.agtms.core.domain.grid;

import java.util.List;

import lombok.Data;

/**
 * 属性编辑对象
 * 
 * @author saisimon
 *
 * @param <T> 属性类型
 */
@Data
public class Editor<T> {
	
	private Object value;
	
	private String className;
	
	private String type;
	
	private String selectionSign;
	
	private List<T> options;
	
	public Editor(Object value, String className) {
		this(value, className, "text");
	}
	
	public Editor(Object value, String className, String type) {
		this(value, className, type, null, null);
	}
	
	public Editor(Object value, String className, String selectionSign, List<T> options) {
		this(value, className, "select", selectionSign, options);
	}
	
	public Editor(Object value, String className, String type, String selectionSign, List<T> options) {
		this.value = value;
		this.className = className;
		this.type = type;
		this.selectionSign = selectionSign;
		this.options = options;
	}
	
}
