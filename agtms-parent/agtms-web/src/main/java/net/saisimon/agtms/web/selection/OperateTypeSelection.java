package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class OperateTypeSelection extends AbstractSelection {
	
	@Override
	public LinkedHashMap<String, String> select() {
		OperateTypes[] types = OperateTypes.values();
		LinkedHashMap<String, String> typeMap = new LinkedHashMap<>(types.length);
		for (OperateTypes type : types) {
			typeMap.put(type.getType().toString(), getMessage(SystemUtils.humpToCode(type.getName(), ".")));
		}
		return typeMap;
	}
	
	@Override
	public Selections key() {
		return Selections.OPERATE_TYPE;
	}
	
}
