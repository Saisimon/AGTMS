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
	
	/**
	 * 获取下拉列表映射集合，key 为下拉列表选项值，value 为下拉列表选项名称
	 * 
	 * @return 下拉列表映射集合
	 */
	Map<T, String> select();
	
	/**
	 * 根据下拉列表选项值集合，获取下拉列表映射集合，key 为下拉列表选项值，value 为下拉列表选项名称
	 * 
	 * @param values
	 * @return 下拉列表映射集合
	 */
	Map<T, String> selectValue(List<T> values);
	
	/**
	 * 根据下拉列表选项名称集合，获取下拉列表映射集合，key 为下拉列表选项值，value 为下拉列表选项名称
	 * 
	 * @param texts
	 * @return 下拉列表映射集合
	 */
	Map<T, String> selectText(List<String> texts);
	
	/**
	 * 根据模糊匹配下拉列表选项名称，获取下拉列表映射集合，key 为下拉列表选项值，value 为下拉列表选项名称
	 * 
	 * @param text
	 * @return 下拉列表映射集合
	 */
	Map<T, String> selectFuzzyText(String text);
	
}
