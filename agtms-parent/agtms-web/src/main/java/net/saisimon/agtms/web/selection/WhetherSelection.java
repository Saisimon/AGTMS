package net.saisimon.agtms.web.selection;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.enums.Whether;
import net.saisimon.agtms.core.selection.AbstractSelection;

@Component
public class WhetherSelection extends AbstractSelection<Integer> {

	@Override
	public Map<Integer, String> select() {
		Whether[] ws = Whether.values();
		Map<Integer, String> whetherMap = MapUtil.newHashMap(ws.length, true);
		for (Whether w : ws) {
			whetherMap.put(w.getValue(), getMessage(w.getName()));
		}
		return whetherMap;
	}
	
}
