package net.saisimon.agtms.core.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.entity.Role;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 角色服务接口
 * 
 * @author saisimon
 *
 */
public interface RoleService extends BaseService<Role, Long>, Ordered {
	
	default boolean exists(Long id, String name, Collection<Long> operatorIds) {
		if (CollectionUtils.isEmpty(operatorIds)) {
			return false;
		}
		FilterRequest filter = FilterRequest.build().and("name", name).and(Constant.OPERATORID, operatorIds, Constant.Operator.IN);
		if (id != null) {
			filter.and(Constant.ID, id, Constant.Operator.NE);
		}
		return exists(filter);
	}

	default Role getRole(String name, Long operatorId) {
		if (SystemUtils.isBlank(name) || operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("name", name).and(Constant.OPERATORID, operatorId);
		return findOne(filter).orElse(null);
	}
	
	default List<Role> getRoles(String excludePath, Collection<Long> operatorIds) {
		if (CollectionUtils.isEmpty(operatorIds)) {
			return Collections.emptyList();
		}
		FilterRequest filter = FilterRequest.build().and(Constant.OPERATORID, operatorIds, Constant.Operator.IN);
		if (excludePath != null) {
			filter.and("path", excludePath, Constant.Operator.RNREGEX);
		}
		FilterSort sort = FilterSort.build("path", Direction.ASC);
		return findList(filter, sort);
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
	@Caching(evict=@CacheEvict(cacheNames={ Constant.Cache.RESOURCE_IDS_NAME, Constant.Cache.USER_IDS_NAME }, allEntries=true), put=@CachePut(cacheNames="role", key="#p0.id"))
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
