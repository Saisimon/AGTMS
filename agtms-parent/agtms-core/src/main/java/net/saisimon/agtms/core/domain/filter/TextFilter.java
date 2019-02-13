package net.saisimon.agtms.core.domain.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.core.domain.tag.Input;
import net.saisimon.agtms.core.domain.tag.SingleSelect;

/**
 * 前端普通文本过滤条件对象
 * 
 * @author saisimon
 *
 * @param <T> value 的类型
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TextFilter<T> extends FieldFilter {
	
	private Input<T> input;
	
	private SingleSelect<String> operator;
	
	public TextFilter() {
		super("text");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		TextFilter<T> textFilter = (TextFilter<T>) super.clone();
		textFilter.input = (Input<T>) textFilter.input.clone();
		textFilter.operator = (SingleSelect<String>) textFilter.operator.clone();
		return textFilter;
	}
	
	public static <T> FieldFilter textFilter(T input, String type, String operator) {
		TextFilter<T> textFilter = new TextFilter<>();
		textFilter.setInput(new Input<>(input, type));
		textFilter.setOperator(SingleSelect.operator(operator));
		return textFilter;
	}
	
}
