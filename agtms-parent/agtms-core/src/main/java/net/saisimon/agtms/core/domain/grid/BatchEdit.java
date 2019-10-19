package net.saisimon.agtms.core.domain.grid;

import java.util.List;
import java.util.Map;

import lombok.Data;
import net.saisimon.agtms.core.domain.tag.Option;

/**
 * 批量编辑对象
 * 
 * @author saisimon
 *
 */
@Data
public class BatchEdit {

	private List<Option<String>> editFieldOptions;
	
	private Map<String, Field<?>> editFields;
	
}
