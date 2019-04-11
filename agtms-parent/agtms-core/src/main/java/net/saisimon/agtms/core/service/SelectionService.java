package net.saisimon.agtms.core.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.SelectionOption;
import net.saisimon.agtms.core.domain.entity.SelectionTemplate;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.factory.TokenFactory;

/**
 * 下拉列表服务接口
 * 
 * @author saisimon
 *
 */
public interface SelectionService extends BaseService<Selection, Long>, Ordered {
	
	default Boolean exists(String title, Long operatorId) {
		Assert.notNull(title, "title can not be null");
		Assert.notNull(operatorId, "operatorId can not be null");
		FilterRequest filter = FilterRequest.build().and("title", title).and(Constant.OPERATORID, operatorId);
		return count(filter) > 0;
	}
	
	default List<Selection> getSelections(Long operatorId) {
		if (operatorId == null) {
			return null;
		}
		UserToken userToken = TokenFactory.get().getToken(operatorId, false);
		FilterRequest filter = FilterRequest.build();
		if (!userToken.isAdmin()) {
			filter.and(Constant.OPERATORID, operatorId);
		}
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
	
	@Override
	@Cacheable(cacheNames="selection", key="#p0")
	default Optional<Selection> findById(Long id) {
		return BaseService.super.findById(id);
	}

	@Override
	@CacheEvict(cacheNames="selection", key="#p0.id")
	default void delete(Selection entity) {
		BaseService.super.delete(entity);
	}

	@Override
	@CachePut(cacheNames="selection", key="#p0.id")
	default Selection saveOrUpdate(Selection entity) {
		return BaseService.super.saveOrUpdate(entity);
	}

	@Override
	@CacheEvict(cacheNames="selection", key="#p0")
	default void update(Long id, Map<String, Object> updateMap) {
		BaseService.super.update(id, updateMap);
	}

	@Override
	@CacheEvict(cacheNames="selection", allEntries=true)
	default void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		BaseService.super.batchUpdate(filter, updateMap);
	}
	
	@Override
	@CacheEvict(cacheNames="selection", allEntries=true)
	default Long delete(FilterRequest filter) {
		return BaseService.super.delete(filter);
	}
	
}
