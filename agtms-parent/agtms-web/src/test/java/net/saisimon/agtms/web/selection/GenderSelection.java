package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;

import org.springframework.boot.test.context.TestComponent;

import net.saisimon.agtms.core.selection.AbstractSelection;

@TestComponent
public class GenderSelection extends AbstractSelection<Integer> {

	@Override
	public LinkedHashMap<Integer, String> select() {
		LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
		map.put(0, "女");
		map.put(1, "男");
		return map;
	}
	
}