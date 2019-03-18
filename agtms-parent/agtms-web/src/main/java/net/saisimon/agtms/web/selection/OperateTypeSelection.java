package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class OperateTypeSelection extends AbstractSelection<Integer> {
	
	@Override
	public LinkedHashMap<Integer, String> select() {
		OperateTypes[] types = OperateTypes.values();
		LinkedHashMap<Integer, String> typeMap = new LinkedHashMap<>(types.length);
		for (OperateTypes type : types) {
			typeMap.put(type.getType(), getMessage(SystemUtils.humpToCode(type.getName(), ".")));
		}
		return typeMap;
	}
	
}
