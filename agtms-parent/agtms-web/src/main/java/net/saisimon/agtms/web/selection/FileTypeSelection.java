package net.saisimon.agtms.web.selection;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class FileTypeSelection extends AbstractSelection<String> {
	
	@Override
	public Map<String, String> select() {
		FileTypes[] types = FileTypes.values();
		Map<String, String> typeMap = MapUtil.newHashMap(types.length, true);
		for (FileTypes type : types) {
			typeMap.put(type.getType(), getMessage(SystemUtils.humpToCode(type.getType(), ".")));
		}
		return typeMap;
	}
	
}
