package net.saisimon.agtms.core.domain.grid;

import java.util.List;

import lombok.Data;
import net.saisimon.agtms.core.domain.tag.Option;

@Data
public class SelectionGrid {
	
	private List<Breadcrumb> breadcrumbs;
	
	private Field<String> title;
	
	private Field<Option<Integer>> type;
	
	private List<OptionField> options;
	
	private TemplateField template;
	
	@Data
	public static class OptionField {
		
		private Field<?> value;
		
		private Field<?> text;
		
	}
	
	@Data
	public static class TemplateField {
		
		private Field<Option<String>> template;
		
		private Field<Option<String>> value;
		
		private Field<Option<String>> text;
		
	}
	
}
