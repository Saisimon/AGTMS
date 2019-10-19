package net.saisimon.agtms.core.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

/**
 * 空下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class EmptySelection extends AbstractSelection<String> {
	
	private static final LinkedHashMap<String, String> EMPTY_SELECTION_MAP = new LinkedHashMap<>();

	@Override
	public LinkedHashMap<String, String> select() {
		return EMPTY_SELECTION_MAP;
	}

}
