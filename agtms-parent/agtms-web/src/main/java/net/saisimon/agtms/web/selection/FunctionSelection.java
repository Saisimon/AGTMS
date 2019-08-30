package net.saisimon.agtms.web.selection;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 功能下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class FunctionSelection extends AbstractSelection<Integer> {
	
	@Override
	public Map<Integer, String> select() {
		return select(Arrays.asList(Functions.values()));
	}
	
	public Map<Integer, String> select(List<Functions> functions) {
		Map<Integer, String> functionMap = MapUtil.newHashMap(functions.size(), true);
		for (Functions function : functions) {
			functionMap.put(function.getCode(), getMessage(SystemUtils.humpToCode(function.getFunction(), ".")));
		}
		return functionMap;
	}
	
}
