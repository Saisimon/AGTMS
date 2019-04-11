package net.saisimon.agtms.core.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.Ordered;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
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
	
	default Boolean exists(Long excludeId, String loginName, String cellphone, String email) {
		FilterRequest filter = FilterRequest.build();
		if (excludeId != null) {
			filter.and(Constant.ID, excludeId, Operator.NE);
		}
		filter.or("loginName", loginName).or("cellphone", cellphone).or("email", email);
		return count(filter) > 0;
	}
	
	default User getUserByLoginNameOrEmail(String loginName, String email) {
		if (SystemUtils.isBlank(loginName) && SystemUtils.isBlank(email)) {
			return null;
		}
		FilterRequest filter = FilterRequest.build();
		if (SystemUtils.isNotBlank(loginName)) {
			filter.or("loginName", loginName);
		}
		if (SystemUtils.isNotBlank(email)) {
			filter.or("email", email);
		}
		Optional<User> optional = findOne(filter);
		return optional.orElse(null);
	}
	
	default User auth(String loginName, String password) {
		if (SystemUtils.isBlank(loginName) || SystemUtils.isBlank(password)) {
			return null;
		}
		User user = getUserByLoginNameOrEmail(loginName, loginName);
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
