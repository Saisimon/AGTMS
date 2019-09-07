package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 类型下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class ClassSelection extends AbstractSelection<String> {
	
	@Override
	public LinkedHashMap<String, String> select() {
		Classes[] cs = Classes.values();
		LinkedHashMap<String, String> classMap = new LinkedHashMap<>(cs.length, 1.0F);
		for (Classes c : cs) {
			classMap.put(c.getKey(), getMessage(SystemUtils.uncapitalize(c.name().toLowerCase())));
		}
		return classMap;
	}

}
