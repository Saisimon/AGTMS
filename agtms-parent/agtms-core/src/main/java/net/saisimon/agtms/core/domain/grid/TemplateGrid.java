package net.saisimon.agtms.core.domain.grid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import net.saisimon.agtms.core.domain.tag.MultipleSelect;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.enums.EditorTypes;

@Data
public class TemplateGrid {
	
	private List<Breadcrumb> breadcrumbs;
	
	private Editor<String> title;
	
	private Table table;
	
	private MultipleSelect<Integer> functionSelect;
	
	private SingleSelect<Long> navigationSelect;
	
	private SingleSelect<String> dataSourceSelect;
	
	private List<Option<String>> classOptions;
	
	private List<Option<String>> viewOptions;
	
	private List<Option<Integer>> whetherOptions;
	
	private List<Option<String>> selectionOptions;
	
	@Data
	public static class Table {
		
		private int idx;
		
		private List<Column> columns;
		
		private List<Map<String, Object>> rows;
		
	}
	
	@Data
	@Builder
	public static class Column {
		
		private String field;
		
		private boolean sortable;
		
		private Integer width;
		
		private Integer ordered;
		
	}
	
	public static Map<String, Object> buildRow(String key, String title, EditorTypes type) {
		Map<String, Object> row = new HashMap<>();
		row.put("key", key);
		row.put("title", title);
		row.put("editor", type.getType());
		return row;
	}
	
}
