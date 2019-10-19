package net.saisimon.agtms.core.domain.grid;

import java.util.Map;
import java.util.Map.Entry;

import cn.hutool.core.map.MapUtil;
import lombok.Data;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.tag.SingleSelect;

/**
 * 过滤对象
 * 
 * @author saisimon
 *
 */
@Data
public class Filter implements Cloneable {
	
	private SingleSelect<String> key;
	
	private Map<String, FieldFilter> value;
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		Filter filter = (Filter) super.clone();
		filter.key = (SingleSelect<String>) key.clone();
		Map<String, FieldFilter> cloneValue = MapUtil.newHashMap(filter.value.size());
		for (Entry<String, FieldFilter> entry : filter.value.entrySet()) {
			cloneValue.put(entry.getKey(), (FieldFilter) entry.getValue().clone());
		}
		filter.value = cloneValue;
		return filter;
	}
	
}
