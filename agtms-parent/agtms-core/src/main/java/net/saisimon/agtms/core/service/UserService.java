package net.saisimon.agtms.core.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 用户服务接口
 * 
 * @author saisimon
 *
 */
public interface UserService extends BaseService<User, Long>, Ordered {
	
	default User getUserByLoginNameOrEmail(String username, String email) {
		if (SystemUtils.isBlank(username) && SystemUtils.isBlank(email)) {
			return null;
		}
		FilterRequest filter = FilterRequest.build();
		if (SystemUtils.isNotBlank(username)) {
			filter.or("loginName", username);
		}
		if (SystemUtils.isNotBlank(email)) {
			filter.or("email", email);
		}
		Optional<User> optional = findOne(filter);
		return optional.isPresent() ? optional.get() : null;
	}
	
	default User auth(String username, String password) {
		if (SystemUtils.isBlank(username) || SystemUtils.isBlank(password)) {
			return null;
		}
		User user = getUserByLoginNameOrEmail(username, username);
		if (user == null) {
			return null;
		}
		String salt = user.getSalt();
		String hmacPwd = AuthUtils.hmac(password, salt);
		if (!hmacPwd.equals(user.getPassword())) {
			return null;
		}
		return user;
	}
	
	default User register(String username, String email, String password) {
		if (SystemUtils.isBlank(username) || SystemUtils.isBlank(email) || SystemUtils.isBlank(password)) {
			return null;
		}
		User user = getUserByLoginNameOrEmail(username, email);
		if (user != null) {
			return null;
		}
		user = new User();
		user.setLoginName(username);
		user.setEmail(email);
		String salt = AuthUtils.createSalt();
		String hmacPwd = AuthUtils.hmac(password, salt);
		Date time = new Date();
		user.setCreateTime(time);
		user.setUpdateTime(time);
		user.setLastLoginTime(time);
		user.setSalt(salt);
		user.setPassword(hmacPwd);
		saveOrUpdate(user);
		return user;
	}
	
}
