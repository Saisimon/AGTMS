package net.saisimon.agtms.core.domain.tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.saisimon.agtms.core.constant.Constant;

/**
 * 前端单选下拉列表
 * 
 * @author saisimon
 *
 * @param <T> value 的类型
 */
@Getter
@Setter
public class SingleSelect<T> extends Select<T> {
	
	public static final List<String> OPERATORS = Arrays.asList(Constant.Filter.STRICT, Constant.Filter.FUZZY, Constant.Filter.SEPARATOR);
	
	private T selected;
	
	public static SingleSelect<String> operator(String selected) {
		return SingleSelect.select(selected, OPERATORS, OPERATORS);
	}
	
	public static <T> SingleSelect<T> select(T selected, List<T> optionValues, List<String> optionTexts) {
		SingleSelect<T> select = new SingleSelect<>();
		List<Option<T>> options = new ArrayList<>();
		for (int i = 0; i < optionValues.size(); i++) {
			T optionValue = optionValues.get(i);
			Option<T> option = new Option<>(optionValue, optionTexts.get(i));
			options.add(option);
		}
		select.setOptions(options);
		select.setSelected(selected);
		return select;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
