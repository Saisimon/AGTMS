package net.saisimon.agtms.core.selection;

import java.util.List;
import java.util.Map;

/**
 * 下拉选择接口
 * 
 * @author saisimon
 *
 */
public interface Selection<T> {
	
	Map<T, String> select();
	
	Map<T, String> selectValue(List<T> values);
	
	Map<T, String> selectText(List<String> texts);
	
	Map<T, String> selectFuzzyText(String text);
	
}
