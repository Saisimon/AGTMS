package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.selection.AbstractSelection;

@Component
public class WhetherSelection extends AbstractSelection {

	@Override
	public LinkedHashMap<String, String> select() {
		LinkedHashMap<String, String> whetherMap = new LinkedHashMap<>(2);
		whetherMap.put("0", getMessage("no"));
		whetherMap.put("1", getMessage("yes"));
		return whetherMap;
	}
	
	@Override
	public Selections key() {
		return Selections.WHETHER;
	}
	
}
