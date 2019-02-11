package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class FunctionSelection extends AbstractSelection {
	
	@Override
	public LinkedHashMap<String, String> select() {
		Functions[] fs = Functions.values();
		LinkedHashMap<String, String> functionMap = new LinkedHashMap<>(fs.length);
		for (Functions functions : fs) {
			functionMap.put(functions.getCode().toString(), getMessage(SystemUtils.humpToCode(functions.getFunction(), ".")));
		}
		return functionMap;
	}
	
	@Override
	public Selections key() {
		return Selections.FUNCTION;
	}
	
}
