package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class HandleStatusSelection extends AbstractSelection<Integer> {
	
	@Override
	public LinkedHashMap<Integer, String> select() {
		HandleStatuses[] statuses = HandleStatuses.values();
		LinkedHashMap<Integer, String> statusMap = new LinkedHashMap<>(statuses.length);
		for (HandleStatuses status : statuses) {
			statusMap.put(status.getStatus(), getMessage(SystemUtils.humpToCode(status.getName(), ".")));
		}
		return statusMap;
	}
	
}
