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
public class NavigationSelection extends AbstractSelection<String> {
	
	@Override
	public Map<String, String> select() {
		NavigationService navigationService = NavigationServiceFactory.get();
		List<Navigation> navigations = navigationService.getNavigations(AuthUtils.getUid());
		Map<String, String> navigationMap = MapUtil.newHashMap(navigations.size() + 1, true);
		navigationMap.put("-1", "/");
		for (Navigation navigation : navigations) {
			navigationMap.put(navigation.getId().toString(), navigation.getTitle());
		}
		return navigationMap;
	}
	
	public Map<String, String> selectWithParent(Long excludeId) {
		NavigationService navigationService = NavigationServiceFactory.get();
		List<Navigation> navigations = navigationService.getNavigations(AuthUtils.getUid());
		LinkedHashMap<String, String> navigationMap = new LinkedHashMap<>();
		parse(navigations, -1L, navigationMap, excludeId);
		LinkedHashMap<String, String> map = new LinkedHashMap<>(navigations.size() + 1);
		map.put("-1", "/");
		map.putAll(navigationMap);
		return map;
	}
	
	private void parse(List<Navigation> navigations, Long parentId, LinkedHashMap<String, String> map, Long excludeId) {
		if (CollectionUtils.isEmpty(navigations)) {
			return;
		}
		if (parentId != -1 && !map.containsKey(parentId.toString())) {
			return;
		}
		List<Navigation> currents = new ArrayList<>();
		List<Navigation> rests = new ArrayList<>();
		for (Navigation pn : navigations) {
			if (!pn.getId().equals(excludeId)) {
				if (pn.getParentId().equals(parentId)) {
					currents.add(pn);
				} else {
					rests.add(pn);
				}
			}
		}
		for (Navigation pn : currents) {
			map.put(pn.getId().toString(), map.getOrDefault(parentId, "") + "/" + pn.getTitle());
			parse(rests, pn.getId(), map, excludeId);
		}
	}
	
}
