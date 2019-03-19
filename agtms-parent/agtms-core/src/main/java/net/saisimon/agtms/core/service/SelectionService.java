package net.saisimon.agtms.core.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.core.domain.filter.FilterRequest;

/**
 * 下拉列表服务接口
 * 
 * @author saisimon
 *
 */
public interface SelectionService extends BaseService<Selection, Long>, Ordered {
	
	default Boolean exists(String title, Long operatorId) {
		if (title == null || operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("title", title).and("operatorId", operatorId);
		return count(filter) > 0;
	}
	
	default List<Selection> getSelections(Long operatorId) {
		if (operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("operatorId", operatorId);
		return findList(filter);
	}
	
	default LinkedHashMap<String, String> getSelectionMap(Long operatorId) {
		List<Selection> selections = getSelections(operatorId);
		LinkedHashMap<String, String> selectionMap = new LinkedHashMap<>();
		if (!CollectionUtils.isEmpty(selections)) {
			for (Selection selection : selections) {
				selectionMap.put(selection.getId().toString(), selection.getTitle());
			}
		}
		return selectionMap;
	}
	
	List<SelectionOption> getSelectionOptions(Long selectionId);
	
	void removeSelectionOptions(Long selectionId);
	
	void removeSelectionTemplate(Long selectionId);
	
	List<SelectionOption> getSelectionOptions(Long selectionId, Set<String> values, boolean isValue);
	
	List<SelectionOption> searchSelectionOptions(Long selectionId, String keyword, Integer size);
	
	SelectionTemplate getSelectionTemplate(Long selectionId);
	
	void saveSelectionOptions(List<SelectionOption> options);
	
	void saveSelectionTemplate(SelectionTemplate template);
	
}
