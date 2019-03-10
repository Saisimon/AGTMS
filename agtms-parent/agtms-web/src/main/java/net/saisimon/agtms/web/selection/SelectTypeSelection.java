package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class SelectTypeSelection extends AbstractSelection<Integer> {
	
	@Override
	public LinkedHashMap<Integer, String> select() {
		SelectTypes[] types = SelectTypes.values();
		LinkedHashMap<Integer, String> typeMap = new LinkedHashMap<>(types.length);
		for (SelectTypes type : types) {
			typeMap.put(type.getType(), getMessage(SystemUtils.humpToCode(type.getName(), ".")));
		}
		return typeMap;
	}
	
}
