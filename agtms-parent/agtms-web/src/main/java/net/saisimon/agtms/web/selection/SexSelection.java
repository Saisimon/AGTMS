package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import net.saisimon.agtms.core.selection.AbstractSelection;

public class SexSelection extends AbstractSelection<Integer> {

	@Override
	public LinkedHashMap<Integer, String> select() {
		LinkedHashMap<Integer, String> selectMap = new LinkedHashMap<>();
		selectMap.put(0, getMessage("woman"));
		selectMap.put(1, getMessage("man"));
		return selectMap;
	}
	
}
