package net.saisimon.agtms.core.domain.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;
import net.saisimon.agtms.core.domain.tag.SingleSelect;

@Data
public class Filter implements Cloneable {
	
	private SingleSelect<String> key;
	
	private Map<String, FieldFilter> value;
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		Filter filter = (Filter) super.clone();
		filter.key = (SingleSelect<String>) key.clone();
		Map<String, FieldFilter> cloneValue = new HashMap<>();
		for (Entry<String, FieldFilter> entry : filter.value.entrySet()) {
			cloneValue.put(entry.getKey(), (FieldFilter) entry.getValue().clone());
		}
		filter.value = cloneValue;
		return filter;
	}
	
}
