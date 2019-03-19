package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.selection.AbstractSelection;

@Component
public class WhetherSelection extends AbstractSelection<Integer> {

	@Override
	public LinkedHashMap<Integer, String> select() {
		LinkedHashMap<Integer, String> whetherMap = new LinkedHashMap<>(2);
		whetherMap.put(0, getMessage("no"));
		whetherMap.put(1, getMessage("yes"));
		return whetherMap;
	}
	
}
