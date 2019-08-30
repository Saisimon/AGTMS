package net.saisimon.agtms.web.selection;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 文件类型下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class FileTypeSelection extends AbstractSelection<String> {
	
	@Override
	public Map<String, String> select() {
		return select(FileTypes.values());
	}
	
	public Map<String, String> importSelect() {
		return select(FileTypes.CSV, FileTypes.XLS, FileTypes.XLSX);
	}
	
	public Map<String, String> exportSelect() {
		return select(FileTypes.CSV, FileTypes.PDF, FileTypes.XLS, FileTypes.XLSX);
	}
	
	private Map<String, String> select(FileTypes... types) {
		Map<String, String> typeMap = MapUtil.newHashMap(types.length, true);
		for (FileTypes type : types) {
			typeMap.put(type.getType(), getMessage(SystemUtils.humpToCode(type.getType(), ".")));
		}
		return typeMap;
	}
	
}
