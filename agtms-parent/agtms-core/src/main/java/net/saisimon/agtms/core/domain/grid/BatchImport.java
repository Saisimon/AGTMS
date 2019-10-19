package net.saisimon.agtms.core.domain.grid;

import java.util.List;

import lombok.Data;
import net.saisimon.agtms.core.domain.tag.Option;

/**
 * 批量导入对象
 * 
 * @author saisimon
 *
 */
@Data
public class BatchImport {
	
	private String importFileName;
	
	private List<Option<String>> importFieldOptions;
	
	private List<Option<String>> importFileTypeOptions;
	
}
