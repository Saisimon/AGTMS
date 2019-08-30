package net.saisimon.agtms.web.selection;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 下拉列表内容来源类型下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class SelectTypeSelection extends AbstractSelection<Integer> {
	
	@Override
	public Map<Integer, String> select() {
		SelectTypes[] types = SelectTypes.values();
		Map<Integer, String> typeMap = MapUtil.newHashMap(types.length, true);
		for (SelectTypes type : types) {
			if (type.getType() < 0) {
				continue;
			}
			typeMap.put(type.getType(), getMessage(SystemUtils.humpToCode(type.getName(), ".")));
		}
		return typeMap;
	}
	
}
