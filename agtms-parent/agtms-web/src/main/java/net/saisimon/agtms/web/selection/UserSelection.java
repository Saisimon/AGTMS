package net.saisimon.agtms.web.selection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.web.service.user.UserInfoService;

/**
 * 用户下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class UserSelection extends AbstractSelection<String> {
	
	@Autowired
	private UserInfoService userInfoService;

	@Override
	public Map<String, String> select() {
		UserService userService = UserServiceFactory.get();
		Long userId = AuthUtils.getUid();
		if (userId == null) {
			return MapUtil.newHashMap(0);
		}
		Set<Long> userIds = userInfoService.getUserIds(userId);
		List<User> users =userService.findList(FilterRequest.build().and(Constant.ID, userIds, Constant.Operator.IN), Constant.ID, "loginName");
		Map<String, String> userMap = MapUtil.newHashMap(users.size(), true);
		for (User user : users) {
			userMap.put(user.getId().toString(), user.getLoginName());
		}
		return userMap;
	}

}
