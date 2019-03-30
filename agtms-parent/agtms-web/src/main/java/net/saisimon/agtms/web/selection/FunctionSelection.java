package net.saisimon.agtms.web.selection;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class FunctionSelection extends AbstractSelection<Integer> {
	
	@Override
	public Map<Integer, String> select() {
		Functions[] fs = Functions.values();
		Map<Integer, String> functionMap = MapUtil.newHashMap(fs.length, true);
		for (Functions functions : fs) {
			functionMap.put(functions.getCode(), getMessage(SystemUtils.humpToCode(functions.getFunction(), ".")));
		}
		return functionMap;
	}
	
}
