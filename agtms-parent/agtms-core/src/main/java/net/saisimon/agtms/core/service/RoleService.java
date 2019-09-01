package net.saisimon.agtms.core.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.Ordered;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.entity.Role;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.util.SystemUtils;

public interface RoleService extends BaseService<Role, Long>, Ordered {

	default Role getRole(String name, Long operatorId) {
		if (SystemUtils.isBlank(name) || operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("name", name).and(Constant.OPERATORID, operatorId);
		return findOne(filter).orElse(null);
	}
	
	default List<Role> getRoles(Long operatorId) {
		if (operatorId == null) {
			return Collections.emptyList();
		}
		FilterRequest filter = FilterRequest.build().and(Constant.OPERATORID, operatorId);
		return findList(filter);
	}
	
	default List<Role> getAllChildrenRoles(Long parentId, String parentPath) {
		String path = "";
		if (SystemUtils.isNotBlank(parentPath)) {
			path = parentPath;
		}
		if (parentId != null) {
			path += "/" + parentId;
		}
		FilterRequest filter = FilterRequest.build().and("path", path, Operator.RREGEX);
		return findList(filter);
	}
	
	@Override
	@Cacheable(cacheNames="role", key="#p0")
	default Optional<Role> findById(Long id) {
		return BaseService.super.findById(id);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames="role", key="#p0.id"),
			@CacheEvict(cacheNames= { Constant.Cache.RESOURCE_IDS_NAME, Constant.Cache.USER_IDS_NAME }, allEntries=true)
	})
	default void delete(Role entity) {
		BaseService.super.delete(entity);
	}

	@Override
	@CachePut(cacheNames="role", key="#p0.id")
	@CacheEvict(cacheNames= { Constant.Cache.RESOURCE_IDS_NAME, Constant.Cache.USER_IDS_NAME }, allEntries=true)
	default Role saveOrUpdate(Role entity) {
		return BaseService.super.saveOrUpdate(entity);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames="role", key="#p0"),
			@CacheEvict(cacheNames= { Constant.Cache.RESOURCE_IDS_NAME, Constant.Cache.USER_IDS_NAME }, allEntries=true)
	})
	default void update(Long id, Map<String, Object> updateMap) {
		BaseService.super.update(id, updateMap);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames= "role", allEntries=true),
			@CacheEvict(cacheNames= { Constant.Cache.RESOURCE_IDS_NAME, Constant.Cache.USER_IDS_NAME }, allEntries=true)
	})
	default void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		BaseService.super.batchUpdate(filter, updateMap);
	}
	
	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames= "role", allEntries=true),
			@CacheEvict(cacheNames= { Constant.Cache.RESOURCE_IDS_NAME, Constant.Cache.USER_IDS_NAME }, allEntries=true)
	})
	default Long delete(FilterRequest filter) {
		return BaseService.super.delete(filter);
	}
	
}
