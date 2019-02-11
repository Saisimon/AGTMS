package net.saisimon.agtms.core.domain.tag;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class MultipleSelect<T> extends Select<T> implements Cloneable {
	
	private List<Option<T>> selected;

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		MultipleSelect<T> filterMultipleSelect = (MultipleSelect<T>) super.clone();
		if (filterMultipleSelect.selected != null) {
			List<Option<T>> cloneSelected = new ArrayList<Option<T>>(filterMultipleSelect.selected.size());
			for (Option<T> selectedOption : filterMultipleSelect.selected) {
				cloneSelected.add((Option<T>) selectedOption.clone());
			}
			filterMultipleSelect.selected = cloneSelected;
		}
		return filterMultipleSelect;
	}
	
	public static <T> Select<T> multipleSelect(List<T> selected, List<T> optionValues, List<String> optionTexts) {
		MultipleSelect<T> select = new MultipleSelect<>();
		int size = selected == null ? 0 : selected.size();
		List<Option<T>> selectedOptions = new ArrayList<Option<T>>(size);
		List<Option<T>> options = new ArrayList<>();
		for (T optionValue : optionValues) {
			Option<T> option = new Option<>(optionValue, optionValue.toString());
			options.add(option);
			if (selected != null && selected.contains(optionValue)) {
				selectedOptions.add(option);
			}
		}
		select.setOptions(options);
		select.setSelected(selectedOptions);
		return select;
	}
	
}
