package net.saisimon.agtms.core.selection;

import java.util.LinkedHashMap;

/**
 * 下拉选择接口
 * 
 * @author saisimon
 *
 */
public interface Selection<T> {
	
	LinkedHashMap<T, String> select();
	
}
