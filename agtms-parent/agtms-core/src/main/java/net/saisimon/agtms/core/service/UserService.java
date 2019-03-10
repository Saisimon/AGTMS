package net.saisimon.agtms.core.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.util.StringUtils;

/**
 * 用户服务接口
 * 
 * @author saisimon
 *
 */
public interface UserService extends BaseService<User, Long>, Ordered {
	
	default User getUserByLoginNameOrEmail(String username, String email) {
		if (username == null || email == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().or("loginName", username).or("email", email);
		Optional<User> optional = findOne(filter);
		return optional.isPresent() ? optional.get() : null;
	}
	
	default User auth(String username, String password) {
		User user = getUserByLoginNameOrEmail(username, username);
		if (user == null) {
			return null;
		}
		String salt = user.getSalt();
		String hmacPwd = StringUtils.hmac(password, salt);
		if (!hmacPwd.equals(user.getPassword())) {
			return null;
		}
		Map<String, Object> updateMap = new HashMap<>();
		Long time = System.currentTimeMillis();
		updateMap.put("updateTime", time);
		updateMap.put("lastLoginTime", time);
		batchUpdate(FilterRequest.build().and(Constant.ID, user.getId()), updateMap);
		return user;
	}
	
	default User register(String username, String email, String password) {
		User user = getUserByLoginNameOrEmail(username, email);
		if (user != null) {
			return null;
		}
		user = new User();
		user.setLoginName(username);
		user.setEmail(email);
		String salt = StringUtils.createSalt();
		String hmacPwd = StringUtils.hmac(password, salt);
		Long time = System.currentTimeMillis();
		user.setCreateTime(time);
		user.setUpdateTime(time);
		user.setLastLoginTime(time);
		user.setSalt(salt);
		user.setPassword(hmacPwd);
		saveOrUpdate(user);
		return user;
	}
	
}
