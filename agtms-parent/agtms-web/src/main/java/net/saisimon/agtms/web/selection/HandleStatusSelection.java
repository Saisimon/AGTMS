package net.saisimon.agtms.web.selection;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 处理状态下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class HandleStatusSelection extends AbstractSelection<Integer> {
	
	@Override
	public Map<Integer, String> select() {
		HandleStatuses[] statuses = HandleStatuses.values();
		Map<Integer, String> statusMap = MapUtil.newHashMap(statuses.length, true);
		for (HandleStatuses status : statuses) {
			statusMap.put(status.getStatus(), getMessage(SystemUtils.humpToCode(status.getName(), ".")));
		}
		return statusMap;
	}
	
}
