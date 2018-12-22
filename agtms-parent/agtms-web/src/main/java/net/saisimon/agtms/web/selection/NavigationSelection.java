package net.saisimon.agtms.web.selection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.enums.Selections;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.util.TokenUtils;

@Component
public class NavigationSelection extends AbstractSelection {
	
	@Override
	public Selections key() {
		return Selections.NAVIGATE;
	}
	
	@Override
	public LinkedHashMap<String, String> select() {
		NavigationService navigationService = NavigationServiceFactory.get();
		long userId = TokenUtils.getUserInfo().getUserId();
		List<Navigation> navigations = navigationService.getNavigations(userId);
		LinkedHashMap<String, String> navigationMap = new LinkedHashMap<>(navigations.size() + 1);
		navigationMap.put("-1", "/");
		for (Navigation navigation : navigations) {
			navigationMap.put(navigation.getId().toString(), navigation.getTitle());
		}
		return navigationMap;
	}
	
	public LinkedHashMap<Long, String> selectWithParent(Long excludeId) {
		NavigationService navigationService = NavigationServiceFactory.get();
		long userId = TokenUtils.getUserInfo().getUserId();
		List<Navigation> navigations = navigationService.getNavigations(userId);
		LinkedHashMap<Long, String> navigationMap = new LinkedHashMap<>(navigations.size());
		parse(navigations, -1L, navigationMap, excludeId);
		LinkedHashMap<Long, String> map = new LinkedHashMap<>(navigations.size() + 1);
		map.put(-1L, "/");
		map.putAll(navigationMap);
		return map;
	}
	
	private void parse(List<Navigation> navigations, Long parentId, LinkedHashMap<Long, String> map, Long excludeId) {
		if (CollectionUtils.isEmpty(navigations) || (parentId != -1 && !map.containsKey(parentId))) {
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
