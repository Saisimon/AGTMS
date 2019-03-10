package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class ViewSelection extends AbstractSelection<String> {

	@Override
	public LinkedHashMap<String, String> select() {
		Views[] vs = Views.values();
		LinkedHashMap<String, String> viewMap = new LinkedHashMap<>(vs.length);
		for (Views v : vs) {
			viewMap.put(v.getView(), getMessage(SystemUtils.humpToCode(v.getView(), ".")));
		}
		return viewMap;
	}
	
}
