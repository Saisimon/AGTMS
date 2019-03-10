package net.saisimon.agtms.core.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.core.domain.filter.FilterRequest;

/**
 * 选择器服务接口
 * 
 * @author saisimon
 *
 */
public interface SelectionService extends BaseService<Selection, Long>, Ordered {
	
	default Selection getSelection(Long id, Long operatorId) {
		if (id == null || operatorId == null) {
			return null;
		}
		Optional<Selection> optional = findById(id);
		if (optional.isPresent()) {
			Selection selection = optional.get();
			if (selection != null && operatorId == selection.getOperatorId()) {
				return selection;
			}
		}
		return null;
	}
	
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
	
	default LinkedHashMap<Long, String> getSelectionMap(Long operatorId) {
		List<Selection> selections = getSelections(operatorId);
		LinkedHashMap<Long, String> selectionMap = new LinkedHashMap<>();
		if (!CollectionUtils.isEmpty(selections)) {
			for (Selection selection : selections) {
				selectionMap.put(selection.getId(), selection.getTitle());
			}
		}
		return selectionMap;
	}
	
	List<SelectionOption> getSelectionOptions(Long selectionId);
	
	void removeSelectionOptions(Long selectionId);
	
	void removeSelectionTemplate(Long selectionId);
	
	List<SelectionOption> getSelectionOptions(Long selectionId, Set<String> values, Set<String> texts);
	
	List<SelectionOption> searchSelectionOptions(Long selectionId, String keyword, Integer size);
	
	SelectionTemplate getSelectionTemplate(Long selectionId);
	
	void saveSelectionOptions(List<SelectionOption> options);
	
	void saveSelectionTemplate(SelectionTemplate template);
	
}
