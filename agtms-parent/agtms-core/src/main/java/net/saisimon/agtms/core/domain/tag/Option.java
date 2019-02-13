package net.saisimon.agtms.core.domain.tag;

import lombok.Data;

/**
 * 前端下拉列表的选项
 * 
 * @author saisimon
 *
 * @param <T> value 的类型
 */
@Data
public class Option<T> implements Cloneable {
	
	public static final Option<String> STRICT = new Option<>("strict", "strict");
	public static final Option<String> FUZZY = new Option<>("fuzzy", "fuzzy");
	public static final Option<String> SEPARATOR = new Option<>("separator", "separator");
	
	private T value;
	
	private String text;
	
	private boolean disable;
	
	public Option(T value, String text) {
		this(value, text, false);
	}
	
	public Option(T value, String text, boolean disable) {
		this.value = value;
		this.text = text;
		this.disable = disable;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
