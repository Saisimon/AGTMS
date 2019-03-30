package net.saisimon.agtms.web.selection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.domain.entity.Navigation;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.util.AuthUtils;

@Component
public class NavigationSelection extends AbstractSelection<Long> {
	
	@Override
	public Map<Long, String> select() {
		NavigationService navigationService = NavigationServiceFactory.get();
		List<Navigation> navigations = navigationService.getNavigations(AuthUtils.getTokenInfo().getUserId());
		Map<Long, String> navigationMap = MapUtil.newHashMap(navigations.size() + 1, true);
		navigationMap.put(-1L, "/");
		for (Navigation navigation : navigations) {
			navigationMap.put(navigation.getId(), navigation.getTitle());
		}
		return navigationMap;
	}
	
	public Map<Long, String> selectWithParent(Long excludeId) {
		NavigationService navigationService = NavigationServiceFactory.get();
		List<Navigation> navigations = navigationService.getNavigations(AuthUtils.getTokenInfo().getUserId());
		LinkedHashMap<Long, String> navigationMap = new LinkedHashMap<>();
		parse(navigations, -1L, navigationMap, excludeId);
		LinkedHashMap<Long, String> map = new LinkedHashMap<>(navigations.size() + 1);
		map.put(-1L, "/");
		map.putAll(navigationMap);
		return map;
	}
	
	private void parse(List<Navigation> navigations, Long parentId, LinkedHashMap<Long, String> map, Long excludeId) {
		if (CollectionUtils.isEmpty(navigations)) {
			return;
		}
		if (parentId != -1 && !map.containsKey(parentId)) {
			return;
		}
		List<Navigation> currents = new ArrayList<>();
		List<Navigation> rests = new ArrayList<>();
		for (Navigation pn : navigations) {
			if (pn.getId() != excludeId) {
				if (pn.getParentId().longValue() == parentId.longValue()) {
					currents.add(pn);
				} else {
					rests.add(pn);
				}
			}
		}
		for (Navigation pn : currents) {
			map.put(pn.getId(), map.getOrDefault(parentId, "") + "/" + pn.getTitle());
			parse(rests, pn.getId(), map, excludeId);
		}
	}
	
}
