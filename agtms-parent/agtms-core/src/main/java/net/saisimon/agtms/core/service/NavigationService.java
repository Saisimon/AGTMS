package net.saisimon.agtms.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.domain.Navigation;

public interface NavigationService extends BaseService<Navigation, Long>, Ordered {
	
	boolean existNavigation(String title, Long operatorId);
	
	Navigation getNavigation(Long id, Long operatorId);
	
	List<Navigation> getNavigations(List<Long> ids, Long operatorId);
	
	List<Navigation> getChildrenNavigations(Long parentId, Long operatorId);
	
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
	
	List<Navigation> getNavigations(Long operatorId);
	
}
