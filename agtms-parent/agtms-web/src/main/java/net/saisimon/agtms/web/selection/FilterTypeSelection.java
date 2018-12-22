package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.FilterTypes;
import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class FilterTypeSelection extends AbstractSelection {

	@Override
	public LinkedHashMap<String, String> select() {
		FilterTypes[] fts = FilterTypes.values();
		LinkedHashMap<String, String> filterTypeMap = new LinkedHashMap<>(fts.length);
		for (FilterTypes ft : fts) {
			filterTypeMap.put(ft.name().toLowerCase(), getMessage(SystemUtils.humpToCode(ft.name().toLowerCase(), ".")));
		}
		return filterTypeMap;
	}

	@Override
	public Selections key() {
		return Selections.FILTER_TYPE;
	}
	
}
