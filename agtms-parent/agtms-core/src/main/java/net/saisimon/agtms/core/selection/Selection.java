package net.saisimon.agtms.core.selection;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 下拉选择接口
 * 
 * @author saisimon
 *
 */
public interface Selection<T> {
	
	LinkedHashMap<T, String> select();
	
	LinkedHashMap<T, String> selectValue(List<T> values);
	
	LinkedHashMap<T, String> selectText(List<String> texts);
	
	LinkedHashMap<T, String> selectFuzzyText(String text);
	
}
