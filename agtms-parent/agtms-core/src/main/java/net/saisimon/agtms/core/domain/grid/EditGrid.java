package net.saisimon.agtms.core.domain.grid;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
public class EditGrid {
	
	private List<Breadcrumb> breadcrumbs;
	
	private List<Field<?>> fields;
	
	
	@Data
	@Builder
	public static class Field<T> {
		
		private String name;
		
		private T value;
		
		private String type;
		
		private String text;
		
		private boolean required;
		
		private String state;
		
		private String view;
		
		List<T> options;
		
	}
	
}
