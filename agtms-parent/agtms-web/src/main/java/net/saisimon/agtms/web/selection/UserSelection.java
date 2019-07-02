package net.saisimon.agtms.web.selection;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;

@Component
public class UserSelection extends AbstractSelection<String> {

	@Override
	public Map<String, String> select() {
		UserService userService = UserServiceFactory.get();
		Long userId = AuthUtils.getUid();
		UserToken userToken = TokenFactory.get().getToken(userId, false);
		if (userToken != null && userToken.isAdmin()) {
			List<User> users = userService.findList(null, "id", "loginName");
			Map<String, String> userMap = MapUtil.newHashMap(users.size(), true);
			for (User user : users) {
				userMap.put(user.getId().toString(), user.getLoginName());
			}
			return userMap;
		} else {
			Map<String, String> userMap = MapUtil.newHashMap(1, true);
			Optional<User> optional = userService.findById(userId);
			if (optional.isPresent()) {
				userMap.put(userId.toString(), optional.get().getLoginName());
			}
			return userMap;
		}
	}

}
