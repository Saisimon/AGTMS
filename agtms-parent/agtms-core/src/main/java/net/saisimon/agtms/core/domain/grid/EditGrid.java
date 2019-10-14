package net.saisimon.agtms.core.domain.grid;

import java.util.Comparator;
import java.util.List;

import lombok.Data;

@Data
public class EditGrid {
	
	private List<Breadcrumb> breadcrumbs;
	
	private List<FieldGroup> groups;
	
	@Data
	public static class FieldGroup {
		
		private String text;
		
		private Integer ordered;
		
		private List<Field<?>> fields;
		
		public static final Comparator<FieldGroup> COMPARATOR = (f1, f2) -> {
			if (f1.getOrdered() == null) {
				return -1;
			}
			if (f2.getOrdered() == null) {
				return 1;
			}
			return f1.getOrdered().compareTo(f2.getOrdered());
		};
		
	}
	
}
