package net.saisimon.agtms.core.domain.grid;

import java.util.List;
import java.util.Map;

import lombok.Data;
import net.saisimon.agtms.core.domain.tag.Option;

@Data
public class BatchGrid {
	
	private BatchEdit batchEdit;
	
	private BatchExport batchExport;
	
	private BatchImport batchImport;
	
	@Data
	public static class BatchEdit {
		
		private List<Option<String>> editFieldOptions;
		
		private Map<String, Field<?>> editFields;
		
	}
	
	@Data
	public static class BatchExport {
		
		private List<Option<String>> exportFieldOptions;
		
		private List<Option<String>> exportFileTypeOptions;
		
	}
	
	@Data
	public static class BatchImport {
		
		private List<Option<String>> importFieldOptions;
		
		private List<Option<String>> importFileTypeOptions;
		
	}
	
}
