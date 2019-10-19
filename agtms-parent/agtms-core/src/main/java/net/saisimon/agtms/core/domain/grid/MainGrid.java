package net.saisimon.agtms.core.domain.grid;

import java.util.Comparator;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import net.saisimon.agtms.core.domain.page.Pageable;

/**
 * 主布局对象
 * 
 * @author saisimon
 *
 */
@Data
public class MainGrid {
	
	private Header header;
	
	private List<Breadcrumb> breadcrumbs;
	
	private List<Column> columns;
	
	private boolean showFilters;
	
	private boolean large;
	
	private List<Filter> filters;
	
	private List<Batch> batches;
	
	private List<Action> actions;
	
	private Pageable pageable;
	
	@Data
	@Builder
	public static class Header {
		
		private String title;
		
		private String createUrl;
		
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
		
		private String views;
		
		private Integer ordered;
		
		public static final Comparator<Column> COMPARATOR = (c1, c2) -> {
			if (c1.getOrdered() == null) {
				return -1;
			}
			if (c2.getOrdered() == null) {
				return 1;
			}
			return c1.getOrdered().compareTo(c2.getOrdered());
		};
		
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
	
	@Data
	@Builder
	public static class Batch {
		
		private String variant;
		
		private String text;
		
		private String icon;
		
		private String key;
		
	}
	
}
