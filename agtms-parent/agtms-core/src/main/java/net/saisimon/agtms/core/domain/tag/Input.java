package net.saisimon.agtms.core.domain.tag;

import lombok.Data;

@Data
public class Input<T> implements Cloneable {
	
	private T value;
	
	private String type;
	
	public Input(T value, String type) {
		this.value = value;
		this.type = type;
	}
	
	public static Input<String> text(String value) {
		return new Input<String>(value, "text");
	}
	
	public static Input<Integer> number(Integer value) {
		return new Input<Integer>(value, "number");
	}
	
	public static Input<String> date(String value) {
		return new Input<String>(value, "date");
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
