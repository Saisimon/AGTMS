package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.StringUtils;

@Component
public class ClassSelection extends AbstractSelection {
	
	@Override
	public LinkedHashMap<String, String> select() {
		Classes[] cs = Classes.values();
		LinkedHashMap<String, String> classMap = new LinkedHashMap<>(cs.length);
		for (Classes c : cs) {
			classMap.put(c.getClazz(), getMessage(StringUtils.uncapitalize(c.name().toLowerCase())));
		}
		return classMap;
	}

	@Override
	public Selections key() {
		return Selections.CLASS;
	}

}
