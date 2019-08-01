package net.saisimon.agtms.core.util;

import java.util.Optional;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.saisimon.agtms.core.domain.entity.Navigation;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.service.NavigationService;

/**
 * 导航相关工具类
 * 
 * @author saisimon
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NavigationUtils {
	
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
		UserToken userToken = TokenFactory.get().getToken(operatorId, false);
		if (userToken != null && userToken.isAdmin()) {
			return navigation;
		}
		if (operatorId.equals(navigation.getOperatorId())) {
			return navigation;
		}
		return null;
	}
	
}
