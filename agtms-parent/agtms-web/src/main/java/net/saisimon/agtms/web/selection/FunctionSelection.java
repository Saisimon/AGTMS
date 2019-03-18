package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class FunctionSelection extends AbstractSelection<Integer> {
	
	@Override
	public LinkedHashMap<Integer, String> select() {
		Functions[] fs = Functions.values();
		LinkedHashMap<Integer, String> functionMap = new LinkedHashMap<>(fs.length);
		for (Functions functions : fs) {
			functionMap.put(functions.getCode(), getMessage(SystemUtils.humpToCode(functions.getFunction(), ".")));
		}
		return functionMap;
	}
	
}
