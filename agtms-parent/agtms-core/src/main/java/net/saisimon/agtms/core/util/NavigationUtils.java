package net.saisimon.agtms.core.util;

import java.util.Optional;

import net.saisimon.agtms.core.domain.entity.Navigation;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.service.NavigationService;

/**
 * @author saisimon
 *
 */
public class NavigationUtils {
	
	private NavigationUtils() {
		throw new IllegalAccessError();
	}
	
	public static Navigation getNavigation(Long id, Long operatorId) {
		if (id == null || operatorId == null) {
			return null;
		}
		NavigationService navigationService = NavigationServiceFactory.get();
		Optional<Navigation> optional = navigationService.findById(id);
		if (!optional.isPresent()) {
			return null;
		}
		Navigation navigation = optional.get();
		if (operatorId.equals(navigation.getOperatorId())) {
			return navigation;
		}
		return null;
	}
	
}
