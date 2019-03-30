package net.saisimon.agtms.core.service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.Ordered;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.StringUtils;

/**
 * 用户服务接口
 * 
 * @author saisimon
 *
 */
public interface UserService extends BaseService<User, Long>, Ordered {
	
	default User getUserByLoginNameOrEmail(String username, String email) {
		if (StringUtils.isBlank(username) && StringUtils.isBlank(email)) {
			return null;
		}
		FilterRequest filter = FilterRequest.build();
		if (StringUtils.isNotBlank(username)) {
			filter.or("loginName", username);
		}
		if (StringUtils.isNotBlank(email)) {
			filter.or("email", email);
		}
		Optional<User> optional = findOne(filter);
		return optional.isPresent() ? optional.get() : null;
	}
	
	default User auth(String username, String password) {
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
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
		Map<String, Object> updateMap = MapUtil.newHashMap(3);
		updateMap.put("lastLoginTime", new Date());
		updateMap.put("token", AuthUtils.createToken());
		updateMap.put("expireTime", AuthUtils.getExpireTime());
		batchUpdate(FilterRequest.build().and(Constant.ID, user.getId()), updateMap);
		return user;
	}
	
	default User register(String username, String email, String password) {
		if (StringUtils.isBlank(username) || StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
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
		user.setExpireTime(AuthUtils.getExpireTime());
		user.setToken(AuthUtils.createToken());
		saveOrUpdate(user);
		return user;
	}
	
	@Override
	@Cacheable(cacheNames="user", key="#p0")
	default Optional<User> findById(Long id) {
		return BaseService.super.findById(id);
	}

	@Override
	@CacheEvict(cacheNames="user", key="#p0.id")
	default void delete(User entity) {
		BaseService.super.delete(entity);
	}

	@Override
	@CachePut(cacheNames="user", key="#p0.id")
	default User saveOrUpdate(User entity) {
		return BaseService.super.saveOrUpdate(entity);
	}

	@Override
	@CacheEvict(cacheNames="user", key="#p0")
	default void update(Long id, Map<String, Object> updateMap) {
		BaseService.super.update(id, updateMap);
	}

	@Override
	@CacheEvict(cacheNames="user", allEntries=true)
	default void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		BaseService.super.batchUpdate(filter, updateMap);
	}
	
	@Override
	@CacheEvict(cacheNames="user", allEntries=true)
	default Long delete(FilterRequest filter) {
		return BaseService.super.delete(filter);
	}
	
}
