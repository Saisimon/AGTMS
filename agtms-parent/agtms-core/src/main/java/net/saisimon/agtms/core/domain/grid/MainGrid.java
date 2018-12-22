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
	
	private List<List<Title>> titles;
	
	private List<Column> columns;
	
	private List<Filter> filters;
	
	private List<String> functions;
	
	private Pageable pageable;
	
	@Data
	@Builder
	public static class Header {
		
		private String title;
		
		private String createUrl;
		
	}
	
	@Data
	@Builder
	public static class Title {
		
		private List<String> fields;
		
		private String title;
		
		@Builder.Default
		private String titleAlign = "center";
		
		/** 列合并数 */
		private Integer colspan;
		
		/** 行合并数 */
		private Integer rowspan;
		
		private String orderBy;
		
	}
	
	@Data
	@Builder
	public static class Column {
		
		private String field;
		
		private String title;
		
		private Integer width;
		
		@Builder.Default
		private String titleAlign = "center";
		
		@Builder.Default
		private String columnAlign = "left";
		
		private String orderBy;
		
		private String view;
		
	}
	
}
