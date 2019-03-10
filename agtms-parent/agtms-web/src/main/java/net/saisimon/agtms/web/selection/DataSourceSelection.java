package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;

@Component
public class DataSourceSelection extends AbstractSelection<String> {

	@Override
	public LinkedHashMap<String, String> select() {
		List<Sign> signs = GenerateServiceFactory.getSigns();
		LinkedHashMap<String, String> dataSourceMap = new LinkedHashMap<>();
		for (Sign sign : signs) {
			if (sign.getName().equals("remote")) {
				continue;
			}
			dataSourceMap.put(sign.getName(), sign.getText() == null ? sign.getName() : sign.getText());
		}
		return dataSourceMap;
	}

}
