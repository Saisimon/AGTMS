package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;

@Component
public class DataSourceSelection extends AbstractSelection {

	@Override
	public LinkedHashMap<String, String> select() {
		List<Sign> signs = GenerateServiceFactory.getSigns();
		LinkedHashMap<String, String> dataSourceMap = new LinkedHashMap<>();
		for (Sign sign : signs) {
			dataSourceMap.put(sign.getName(), getMessage(sign.getText() == null ? sign.getName() : sign.getText()));
		}
		return dataSourceMap;
	}

	@Override
	public Selections key() {
		return Selections.DATA_SOURCE;
	}
	
}
