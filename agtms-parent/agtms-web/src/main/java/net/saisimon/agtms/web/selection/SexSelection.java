package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.selection.AbstractSelection;

public class SexSelection extends AbstractSelection {

	@Override
	public LinkedHashMap<String, String> select() {
		LinkedHashMap<String, String> selectMap = new LinkedHashMap<>();
		selectMap.put("0", getMessage("woman"));
		selectMap.put("1", getMessage("man"));
		return selectMap;
	}
	
	@Override
	public Selections key() {
		return Selections.SEX;
	}

}
