package net.saisimon.agtms.core.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.Ordered;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.entity.UserRole;
import net.saisimon.agtms.core.domain.filter.FilterRequest;

/**
 * 用户角色关系服务接口
 * 
 * @author saisimon
 *
 */
public interface UserRoleService extends BaseService<UserRole, Long>, Ordered {
	
	default boolean exists(Long userId, Long roleId) {
		if (userId == null || roleId == null) {
			return false;
		}
		FilterRequest filter = FilterRequest.build().and("userId", userId).and("roleId", roleId);
		return count(filter) > 0;
	}
	
	default List<UserRole> getUserRoles(Long userId) {
		if (userId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("userId", userId);
		return findList(filter);
	}
	
	default List<UserRole> getUserRoles(Collection<Long> roleIds) {
		if (CollectionUtils.isEmpty(roleIds)) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("roleId", roleIds, Operator.IN);
		return findList(filter);
	}
	
	@Override
	@Cacheable(cacheNames="userRole", key="#p0")
	default Optional<UserRole> findById(Long id) {
		return BaseService.super.findById(id);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames= "userRole", key="#p0.id"),
			@CacheEvict(cacheNames= { Constant.Cache.RESOURCE_IDS_NAME, Constant.Cache.USER_IDS_NAME }, key="#p0.userId")
	})
	default void delete(UserRole entity) {
		BaseService.super.delete(entity);
	}

	@Override
	@Caching(evict=@CacheEvict(cacheNames= { Constant.Cache.RESOURCE_IDS_NAME, Constant.Cache.USER_IDS_NAME }, key="#p0.userId"), put=@CachePut(cacheNames="userRole", key="#p0.id"))
	default UserRole saveOrUpdate(UserRole entity) {
		return BaseService.super.saveOrUpdate(entity);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames= "userRole", key="#p0"),
			@CacheEvict(cacheNames= { Constant.Cache.RESOURCE_IDS_NAME, Constant.Cache.USER_IDS_NAME }, allEntries=true)
	})
	default void update(Long id, Map<String, Object> updateMap) {
		BaseService.super.update(id, updateMap);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames= "userRole", allEntries=true),
			@CacheEvict(cacheNames= { Constant.Cache.RESOURCE_IDS_NAME, Constant.Cache.USER_IDS_NAME }, allEntries=true)
	})
	default void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		BaseService.super.batchUpdate(filter, updateMap);
	}
	
	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames= "userRole", allEntries=true),
			@CacheEvict(cacheNames= { Constant.Cache.RESOURCE_IDS_NAME, Constant.Cache.USER_IDS_NAME }, allEntries=true)
	})
	default Long delete(FilterRequest filter) {
		return BaseService.super.delete(filter);
	}
	
}
