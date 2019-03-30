package net.saisimon.agtms.web.selection;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.selection.AbstractSelection;

@Component
public class WhetherSelection extends AbstractSelection<Integer> {

	@Override
	public Map<Integer, String> select() {
		Map<Integer, String> whetherMap = MapUtil.newHashMap(2, true);
		whetherMap.put(0, getMessage("no"));
		whetherMap.put(1, getMessage("yes"));
		return whetherMap;
	}
	
}
