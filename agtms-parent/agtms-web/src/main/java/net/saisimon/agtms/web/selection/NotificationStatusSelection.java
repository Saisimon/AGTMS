package net.saisimon.agtms.web.selection;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.enums.NotificationStatuses;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 操作类型下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class NotificationStatusSelection extends AbstractSelection<Integer> {
	
	@Override
	public Map<Integer, String> select() {
		NotificationStatuses[] statuses = NotificationStatuses.values();
		Map<Integer, String> statusMap = MapUtil.newHashMap(statuses.length, true);
		for (NotificationStatuses status : statuses) {
			statusMap.put(status.getStatus(), getMessage(SystemUtils.humpToCode(status.getName(), ".")));
		}
		return statusMap;
	}
	
}
