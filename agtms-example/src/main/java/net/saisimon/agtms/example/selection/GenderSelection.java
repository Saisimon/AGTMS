package net.saisimon.agtms.example.selection;

import java.util.LinkedHashMap;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.selection.AbstractSelection;

@Component
public class GenderSelection extends AbstractSelection<Integer> {

	@Override
	public LinkedHashMap<Integer, String> select() {
		LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
		map.put(0, "女");
		map.put(1, "男");
		return map;
	}
	
}
