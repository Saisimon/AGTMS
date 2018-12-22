package net.saisimon.agtms.core.domain.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SingleSelect<T> extends Select<T> implements Cloneable {
	
	private static final List<String> OPERATORS = Arrays.asList("strict", "fuzzy", "separator");
	
	private Option<T> selected;
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		SingleSelect<T> filterSingleSelect = (SingleSelect<T>) super.clone();
		if (filterSingleSelect.selected != null) {
			filterSingleSelect.selected = (Option<T>) filterSingleSelect.selected.clone();
		}
		return filterSingleSelect;
	}
	
	public static SingleSelect<String> operator(String selected) {
		return SingleSelect.select(selected, OPERATORS, OPERATORS);
	}
	
	public static <T> SingleSelect<T> select(T selected, List<T> optionValues, List<String> optionTexts) {
		SingleSelect<T> select = new SingleSelect<>();
		List<Option<T>> options = new ArrayList<>();
		Class<?> javaType = null;
		for (int i = 0; i < optionValues.size(); i++) {
			T optionValue = optionValues.get(i);
			Option<T> option = new Option<>(optionValue, optionTexts.get(i));
			options.add(option);
			if (optionValue.equals(selected)) {
				select.setSelected(option);
			}
			javaType = optionValue.getClass();
		}
		select.setOptions(options);
		select.setJavaType(javaType);
		return select;
	}
	
}
