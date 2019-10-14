package net.saisimon.agtms.core.domain.tag;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 前端多选下拉列表
 * 
 * @author saisimon
 *
 * @param <T> value 的类型
 */
@Getter
@Setter
public class MultipleSelect<T> extends Select<T> {
	
	private List<T> selected;

	public static <T> Select<T> multipleSelect(List<T> selected, List<T> optionValues, List<String> optionTexts) {
		MultipleSelect<T> select = new MultipleSelect<>();
		List<Option<T>> options = new ArrayList<>();
		for (T optionValue : optionValues) {
			Option<T> option = new Option<>(optionValue, optionValue.toString());
			options.add(option);
		}
		select.setOptions(options);
		select.setSelected(selected);
		return select;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		MultipleSelect<T> filterMultipleSelect = (MultipleSelect<T>) super.clone();
		if (this.selected != null) {
			List<T> cloneSelected = new ArrayList<>(this.selected.size());
			for (T selectedOption : this.selected) {
				cloneSelected.add(selectedOption);
			}
			filterMultipleSelect.selected = cloneSelected;
		}
		return filterMultipleSelect;
	}
	
}
