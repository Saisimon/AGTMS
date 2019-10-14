package net.saisimon.agtms.core.service;

import java.util.Collection;
import java.util.Collections;
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
import net.saisimon.agtms.core.domain.entity.RoleResource;
import net.saisimon.agtms.core.domain.filter.FilterRequest;

public interface RoleResourceService extends BaseService<RoleResource, Long>, Ordered {
	
	default boolean exists(Long roleId, Long resourceId) {
		if (roleId == null || resourceId == null) {
			return false;
		}
		FilterRequest filter = FilterRequest.build().and("roleId", roleId).and("resourceId", resourceId);
		return count(filter) > 0;
	}
	
	default List<RoleResource> getRoleResources(Collection<Long> roleIds, Long resourceId) {
		if (CollectionUtils.isEmpty(roleIds)) {
			return Collections.emptyList();
		}
		FilterRequest filter = FilterRequest.build();
		if (roleIds.size() == 1) {
			filter.and("roleId", roleIds.iterator().next());
		} else {
			filter.and("roleId", roleIds, Operator.IN);
		}
		if (resourceId != null) {
			filter.and("resourceId", resourceId);
		}
		return findList(filter);
	}
	
	@Override
	@Cacheable(cacheNames="roleResource", key="#p0")
	default Optional<RoleResource> findById(Long id) {
		return BaseService.super.findById(id);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames="roleResource", key="#p0.id"),
			@CacheEvict(cacheNames=Constant.Cache.RESOURCE_IDS_NAME, allEntries=true)
	})
	default void delete(RoleResource entity) {
		BaseService.super.delete(entity);
	}

	@Override
	@Caching(evict = @CacheEvict(cacheNames=Constant.Cache.RESOURCE_IDS_NAME, allEntries=true, beforeInvocation=true), put = @CachePut(cacheNames="roleResource", key="#p0.id"))
	default RoleResource saveOrUpdate(RoleResource entity) {
		return BaseService.super.saveOrUpdate(entity);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames="roleResource", key="#p0"),
			@CacheEvict(cacheNames=Constant.Cache.RESOURCE_IDS_NAME, allEntries=true)
	})
	default void update(Long id, Map<String, Object> updateMap) {
		BaseService.super.update(id, updateMap);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames= "roleResource", allEntries=true),
			@CacheEvict(cacheNames=Constant.Cache.RESOURCE_IDS_NAME, allEntries=true)
	})
	default void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		BaseService.super.batchUpdate(filter, updateMap);
	}
	
	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames= "roleResource", allEntries=true),
			@CacheEvict(cacheNames=Constant.Cache.RESOURCE_IDS_NAME, allEntries=true)
	})
	default Long delete(FilterRequest filter) {
		return BaseService.super.delete(filter);
	}
	
}
