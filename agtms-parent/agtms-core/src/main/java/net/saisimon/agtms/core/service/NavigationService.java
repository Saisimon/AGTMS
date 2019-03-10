package net.saisimon.agtms.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.cache.Cache;
import net.saisimon.agtms.core.domain.entity.Navigation;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.factory.CacheFactory;

/**
 * 导航服务接口
 * 
 * @author saisimon
 *
 */
public interface NavigationService extends BaseService<Navigation, Long>, Ordered {
	
	public static final String NAVIGATION_KEY = "navigation_%s";
	public static final long NAVIGATION_TIMEOUT = 60 * 1000L;
	
	default Boolean exists(String title, Long operatorId) {
		if (title == null || operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("title", title).and("operatorId", operatorId);
		return count(filter) > 0;
	}
	
	default Navigation getNavigation(Long id, Long operatorId) {
		if (id == null || operatorId == null) {
			return null;
		}
		String key = String.format(NAVIGATION_KEY, id.toString());
		Cache cache = CacheFactory.get();
		Navigation navigation = cache.get(key, Navigation.class);
		if (navigation == null) {
			Optional<Navigation> optional = findById(id);
			if (optional.isPresent()) {
				navigation = optional.get();
				cache.set(key, navigation, NAVIGATION_TIMEOUT);
			}
		}
		if (navigation != null && operatorId == navigation.getOperatorId()) {
			return navigation;
		}
		return null;
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
		Map<Long, Navigation> navigationMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(navigations)) {
			for (Navigation navigation : navigations) {
				navigationMap.put(navigation.getId(), navigation);
			}
		}
		return navigationMap;
	}
	
	@Override
	default void delete(Navigation entity) {
		BaseService.super.delete(entity);
		Cache cache = CacheFactory.get();
		cache.delete(String.format(NAVIGATION_KEY, entity.getId()));
	}
	
	@Override
	default Navigation saveOrUpdate(Navigation entity) {
		Navigation navigation = BaseService.super.saveOrUpdate(entity);
		if (navigation != null) {
			String key = String.format(NAVIGATION_KEY, navigation.getId());
			Cache cache = CacheFactory.get();
			cache.set(key, navigation, NAVIGATION_TIMEOUT);
		}
		return navigation;
	}
	
}
