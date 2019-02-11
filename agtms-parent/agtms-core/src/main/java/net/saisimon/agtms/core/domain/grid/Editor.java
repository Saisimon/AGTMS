package net.saisimon.agtms.core.domain.grid;

import lombok.Data;

@Data
public class Editor<T> {
	
	private T value;
	
	public Editor(T value) {
		this.value = value;
	}
	
}
