package net.saisimon.agtms.core.domain.grid;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import net.saisimon.agtms.core.domain.filter.Filter;
import net.saisimon.agtms.core.domain.page.Pageable;

@Data
public class MainGrid {
	
	private Header header;
	
	private List<Breadcrumb> breadcrumbs;
	
	private List<Column> columns;
	
	private boolean showFilters;
	
	private List<Filter> filters;
	
	private List<String> functions;
	
	private List<Action> actions;
	
	private Pageable pageable;
	
	@Data
	@Builder
	public static class Header {
		
		private String title;
		
		private String createUrl;
		
		private String editUrl;
		
	}
	
	@Data
	@Builder
	public static class Column {
		
		private String field;
		
		private String label;
		
		private String type;
		
		private String dateInputFormat;
		
		private String dateOutputFormat;
		
		private boolean sortable;
		
		private String orderBy;
		
		private Integer width;
		
		private String view;
		
		private Integer ordered;
		
	}
	
	@Data
	@Builder
	public static class Action {
		
		private String to;
		
		@Builder.Default
		private String variant = "outline-primary";
		
		private String text;
		
		private String icon;
		
		private String type;
		
		private String key;
		
	}
	
}
