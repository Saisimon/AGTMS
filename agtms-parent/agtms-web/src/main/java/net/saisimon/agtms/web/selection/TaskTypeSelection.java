package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.factory.ActuatorFactory;

@Component
public class TaskTypeSelection extends AbstractSelection<String> {
	
	@Override
	public LinkedHashMap<String, String> select() {
		List<Sign> signs = ActuatorFactory.getSigns();
		LinkedHashMap<String, String> dataSourceMap = new LinkedHashMap<>();
		for (Sign sign : signs) {
			dataSourceMap.put(sign.getName(), getMessage(sign.getText() == null ? sign.getName() : sign.getText()));
		}
		return dataSourceMap;
	}
	
}
