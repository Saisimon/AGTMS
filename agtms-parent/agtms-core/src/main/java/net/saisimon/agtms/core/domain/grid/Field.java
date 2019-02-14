package net.saisimon.agtms.core.domain.grid;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Field<T> {
	
	private String name;
	
	private T value;
	
	private String type;
	
	private String text;
	
	private boolean required;
	
	private String state;
	
	private String view;
	
	private List<T> options;
	
	private Integer ordered;
	
}
