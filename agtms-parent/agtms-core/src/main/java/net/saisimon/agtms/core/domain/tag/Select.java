package net.saisimon.agtms.core.domain.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Data;

/**
 * 前端下拉列表
 * 
 * @author saisimon
 *
 * @param <T> value 的类型
 */
@Data
public class Select<T> implements Cloneable {
	
	private List<Option<T>> options;
	
	private String type;
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		Select<T> filterSelect = (Select<T>) super.clone();
		if (this.options != null) {
			List<Option<T>> cloneOptions = new ArrayList<>();
			for (Option<T> option : this.options) {
				cloneOptions.add((Option<T>) option.clone());
			}
			filterSelect.options = cloneOptions;
		}
		return filterSelect;
	}
	
	public static <T> List<Option<T>> buildOptions(Map<T, String> selectionMap) {
		if (selectionMap == null) {
			return null;
		}
		List<Option<T>> options = new ArrayList<>(selectionMap.size());
		for (Entry<T, String> entry : selectionMap.entrySet()) {
			Option<T> option = new Option<>(entry.getKey(), entry.getValue());
			options.add(option);
		}
		return options;
	}
	
}
