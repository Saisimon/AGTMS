package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.FileTypes;
import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class FileTypeSelection extends AbstractSelection {
	
	@Override
	public LinkedHashMap<String, String> select() {
		FileTypes[] types = FileTypes.values();
		LinkedHashMap<String, String> typeMap = new LinkedHashMap<>(types.length);
		for (FileTypes type : types) {
			typeMap.put(type.getType().toString(), getMessage(SystemUtils.humpToCode(type.getType(), ".")));
		}
		return typeMap;
	}
	
	@Override
	public Selections key() {
		return Selections.FILE_TYPE;
	}
	
}
