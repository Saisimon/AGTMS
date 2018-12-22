package net.saisimon.agtms.jpa.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.domain.User;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.order.AbstractOrder;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.jpa.repository.UserJpaRepository;

@Service
public class UserJpaService extends AbstractOrder implements UserService {
	
	@Autowired
	private UserJpaRepository userJpaRepository;
	
	@Override
	public BaseRepository<User, Long> getRepository() {
		return userJpaRepository;
	}
	
	@Override
	public User auth(String username, String password) {
		User user = userJpaRepository.findByLoginNameOrEmail(username, username);
		if (user == null) {
			return null;
		}
		String salt = user.getSalt();
		String hmacPwd = StringUtils.hmac(password, salt);
		if (!hmacPwd.equals(user.getPassword())) {
			return null;
		}
		Map<String, Object> updateMap = new HashMap<>();
		updateMap.put("lastLoginTime", System.currentTimeMillis());
		userJpaRepository.batchUpdate(FilterRequest.build().and("id", user.getId()), updateMap);
		return user;
	}

	@Override
	public User register(String username, String email, String password) {
		User user = userJpaRepository.findByLoginNameOrEmail(username, email);
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
		userJpaRepository.save(user);
		return user;
	}

}
