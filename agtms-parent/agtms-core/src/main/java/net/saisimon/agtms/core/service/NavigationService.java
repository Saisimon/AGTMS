package net.saisimon.agtms.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.domain.Navigation;

public interface NavigationService extends BaseService<Navigation, Long>, Ordered {
	
	boolean existNavigation(String title, Long userId);
	
	Navigation getNavigation(Long id, Long userId);
	
	List<Navigation> getNavigations(List<Long> ids, Long userId);
	
	List<Navigation> getChildrenNavigations(Long parentId, Long userId);
	
	default Map<Long, Navigation> getNavigationMap(Long userId) {
		List<Navigation> navigations = getNavigations(userId);
		Map<Long, Navigation> navigationMap = new HashMap<>();
		if (!CollectionUtils.isEmpty(navigations)) {
			for (Navigation navigation : navigations) {
				navigationMap.put(navigation.getId(), navigation);
			}
		}
		return navigationMap;
	}
	
	List<Navigation> getNavigations(Long userId);
	
}
