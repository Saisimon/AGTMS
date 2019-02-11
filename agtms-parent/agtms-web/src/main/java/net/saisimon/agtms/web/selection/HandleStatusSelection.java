package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class HandleStatusSelection extends AbstractSelection {
	
	@Override
	public LinkedHashMap<String, String> select() {
		HandleStatuses[] statuses = HandleStatuses.values();
		LinkedHashMap<String, String> statusMap = new LinkedHashMap<>(statuses.length);
		for (HandleStatuses status : statuses) {
			statusMap.put(status.getStatus().toString(), getMessage(SystemUtils.humpToCode(status.getName(), ".")));
		}
		return statusMap;
	}
	
	@Override
	public Selections key() {
		return Selections.HANDLE_STATUS;
	}
	
}
