package net.saisimon.agtms.core.domain.grid;

import java.util.Comparator;
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
	
	private boolean disabled;
	
	private String state;
	
	private String view;
	
	private Long selectionId;
	
	private boolean searchable;
	
	private List<T> options;
	
	private Integer ordered;
	
	public static final Comparator<Field<?>> COMPARATOR = (f1, f2) -> {
		if (f1.getOrdered() == null) {
			return -1;
		}
		if (f2.getOrdered() == null) {
			return 1;
		}
		return f1.getOrdered().compareTo(f2.getOrdered());
	};
	
}
