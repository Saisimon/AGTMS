package net.saisimon.agtms.web.selection;

import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class UserStatusSelection extends AbstractSelection<Integer> {
	
	@Override
	public Map<Integer, String> select() {
		UserStatuses[] statuses = UserStatuses.values();
		Map<Integer, String> statusMap = MapUtil.newHashMap(statuses.length, true);
		for (UserStatuses status : statuses) {
			statusMap.put(status.getStatus(), getMessage(SystemUtils.humpToCode(status.getName(), ".")));
		}
		return statusMap;
	}
	
}
