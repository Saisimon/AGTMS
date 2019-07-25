package net.saisimon.agtms.core.domain.grid;

import java.util.List;

import lombok.Data;

@Data
public class Editor<T> {
	
	private T value;
	
	private String className;
	
	private String type;
	
	private List<T> options;
	
	public Editor(T value, String className) {
		this(value, className, "text");
	}
	
	public Editor(T value, String className, String type) {
		this(value, className, type, null);
	}
	
	public Editor(T value, String className, List<T> options) {
		this(value, className, "select", options);
	}
	
	public Editor(T value, String className, String type, List<T> options) {
		this.value = value;
		this.className = className;
		this.type = type;
		this.options = options;
	}
	
}
