package net.saisimon.agtms.core.domain.grid;

import java.util.List;

import lombok.Data;
import net.saisimon.agtms.core.domain.tag.Option;

@Data
public class BatchExport {
	
	private String exportFileName;
	
	private List<Option<String>> exportFieldOptions;
	
	private List<Option<String>> exportFileTypeOptions;
	
}
