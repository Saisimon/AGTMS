package net.saisimon.agtms.mongodb.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.User;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.mongodb.order.MongodbOrder;
import net.saisimon.agtms.mongodb.repository.UserMongodbRepository;

@Service
public class UserMongodbService implements UserService, MongodbOrder {

	@Autowired
	private UserMongodbRepository userMongodbRepository;
	@Autowired
	private SequenceService sequenceService;
	
	@Override
	public BaseRepository<User, Long> getRepository() {
		return userMongodbRepository;
	}
	
	@Override
	public User auth(String username, String password) {
		User user = userMongodbRepository.findByLoginNameOrEmail(username, username);
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
		batchUpdate(FilterRequest.build().and(Constant.ID, user.getId()), updateMap);
		return user;
	}

	@Override
	public User register(String username, String email, String password) {
		User user = userMongodbRepository.findByLoginNameOrEmail(username, email);
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

	@Override
	public User saveOrUpdate(User entity) {
		if (entity.getId() == null) {
			Long id = sequenceService.nextId(userMongodbRepository.getCollectionName());
			entity.setId(id);
		}
		return UserService.super.saveOrUpdate(entity);
	}
	
}
