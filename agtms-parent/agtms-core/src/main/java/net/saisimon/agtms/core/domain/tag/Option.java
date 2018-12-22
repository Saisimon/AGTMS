package net.saisimon.agtms.core.domain.tag;

import lombok.Data;

@Data
public class Option<T> implements Cloneable {
	
	public static final Option<String> STRICT = new Option<>("strict", "strict");
	public static final Option<String> FUZZY = new Option<>("fuzzy", "fuzzy");
	public static final Option<String> SEPARATOR = new Option<>("separator", "separator");
	
	private T value;
	
	private String text;
	
	public Option(T value, String text) {
		this.value = value;
		this.text = text;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
