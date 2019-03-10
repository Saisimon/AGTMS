package net.saisimon.agtms.core.domain.filter;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import net.saisimon.agtms.core.domain.tag.MultipleSelect;
import net.saisimon.agtms.core.domain.tag.Select;
import net.saisimon.agtms.core.domain.tag.SingleSelect;

/**
 * 前端下拉列表过滤条件的对象
 * 
 * @author saisimon
 *
 * @param <T> value 的类型
 */
@Setter
@Getter
public class SelectFilter<T> extends FieldFilter {
	
	private Select<T> select;
	
	private boolean multiple;
	
	private boolean searchable;
	
	private Long selectionId;
	
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
	
	public static <T> FieldFilter selectFilter(T selected, String type, List<T> optionValues, List<String> optionTexts) {
		SelectFilter<T> selectFilter = new SelectFilter<>(false);
		Select<T> select = SingleSelect.select(selected, optionValues, optionTexts);
		select.setType(type);
		selectFilter.setSelect(select);
		return selectFilter;
	}
	
	public static <T> FieldFilter selectSearchableFilter(T selected, String type, Long selectionId) {
		SelectFilter<T> selectFilter = new SelectFilter<>(false);
		Select<T> select = SingleSelect.select(selected, new ArrayList<>(), new ArrayList<>());
		select.setType(type);
		selectFilter.setSelect(select);
		selectFilter.setSearchable(true);
		selectFilter.setSelectionId(selectionId);
		return selectFilter;
	}
	
	public static <T> FieldFilter multipleSelectFilter(List<T> selected, String type, List<T> optionValues, List<String> optionTexts) {
		SelectFilter<T> selectFilter = new SelectFilter<>(true);
		Select<T> select = MultipleSelect.multipleSelect(selected, optionValues, optionTexts);
		select.setType(type);
		selectFilter.setSelect(MultipleSelect.multipleSelect(selected, optionValues, optionTexts));
		return selectFilter;
	}
	
}
