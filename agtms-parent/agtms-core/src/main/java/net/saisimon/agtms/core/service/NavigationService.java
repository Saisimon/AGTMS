package net.saisimon.agtms.core.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.domain.entity.Navigation;
import net.saisimon.agtms.core.domain.filter.FilterRequest;

/**
 * 导航服务接口
 * 
 * @author saisimon
 *
 */
public interface NavigationService extends BaseService<Navigation, Long>, Ordered {
	
	default Boolean exists(String title, Long operatorId) {
		if (title == null || operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("title", title).and("operatorId", operatorId);
		return count(filter) > 0;
	}
	
	default List<Navigation> getNavigations(Long operatorId) {
		if (operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("operatorId", operatorId);
		return findList(filter);
	}
	
	default List<Navigation> getChildrenNavigations(Long parentId, Long operatorId) {
		if (parentId == null || operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("parentId", parentId).and("operatorId", operatorId);
		return findList(filter);
	}
	
	default Map<Long, Navigation> getNavigationMap(Long operatorId) {
		List<Navigation> navigations = getNavigations(operatorId);
		if (CollectionUtils.isEmpty(navigations)) {
			return MapUtil.newHashMap(0);
		}
		Map<Long, Navigation> navigationMap = MapUtil.newHashMap(navigations.size());
		for (Navigation navigation : navigations) {
			navigationMap.put(navigation.getId(), navigation);
		}
		return navigationMap;
	}

	@Override
	@Cacheable(cacheNames="navigation", key="#p0")
	default Optional<Navigation> findById(Long id) {
		return BaseService.super.findById(id);
	}

	@Override
	@CacheEvict(cacheNames="navigation", key="#p0.id")
	default void delete(Navigation entity) {
		BaseService.super.delete(entity);
	}

	@Override
	@CachePut(cacheNames="navigation", key="#p0.id")
	default Navigation saveOrUpdate(Navigation entity) {
		return BaseService.super.saveOrUpdate(entity);
	}

	@Override
	@CacheEvict(cacheNames="navigation", key="#p0")
	default void update(Long id, Map<String, Object> updateMap) {
		BaseService.super.update(id, updateMap);
	}

	@Override
	@CacheEvict(cacheNames="navigation", allEntries=true)
	default void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		BaseService.super.batchUpdate(filter, updateMap);
	}
	
	@Override
	@CacheEvict(cacheNames="navigation", allEntries=true)
	default Long delete(FilterRequest filter) {
		return BaseService.super.delete(filter);
	}
	
}
