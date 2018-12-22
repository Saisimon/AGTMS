package net.saisimon.agtms.core.domain.filter;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.saisimon.agtms.core.domain.tag.MultipleSelect;
import net.saisimon.agtms.core.domain.tag.Select;
import net.saisimon.agtms.core.domain.tag.SingleSelect;

@Data
@EqualsAndHashCode(callSuper=false)
public class SelectFilter<T> extends FieldFilter {
	
	private Select<T> select;
	
	private Boolean multiple;
	
	public SelectFilter(boolean multiple) {
		super("select");
		this.multiple = multiple;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		SelectFilter<T> selectFilter = (SelectFilter<T>) super.clone();
		if (selectFilter.multiple) {
			selectFilter.select = (MultipleSelect<T>) selectFilter.select.clone();
		} else {
			selectFilter.select = (SingleSelect<T>) selectFilter.select.clone();
		}
		return selectFilter;
	}
	
	public static <T> FieldFilter selectFilter(T selected, List<T> optionValues, List<String> optionTexts) {
		SelectFilter<T> selectFilter = new SelectFilter<>(false);
		selectFilter.setSelect(SingleSelect.select(selected, optionValues, optionTexts));
		return selectFilter;
	}
	
	public static <T> FieldFilter multipleSelectFilter(List<T> selected, List<T> optionValues, List<String> optionTexts) {
		SelectFilter<T> selectFilter = new SelectFilter<>(true);
		selectFilter.setSelect(MultipleSelect.multipleSelect(selected, optionValues, optionTexts));
		return selectFilter;
	}
	
}
