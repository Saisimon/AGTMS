package net.saisimon.agtms.core.domain.grid;

import lombok.Data;

@Data
public class Editor<T> {
	
	private T value;
	
	private String className;
	
	public Editor(T value, String className) {
		this.value = value;
		this.className = className;
	}
	
}
