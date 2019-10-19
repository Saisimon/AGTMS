package net.saisimon.agtms.core.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 资源服务接口
 * 
 * @author saisimon
 *
 */
public interface ResourceService extends BaseService<Resource, Long>, Ordered {
	
	default Boolean exists(String name, Resource.ContentType contentType, Collection<Long> operatorIds) {
		Assert.notNull(name, "name can not be null");
		Assert.notNull(operatorIds, "operatorId can not be null");
		FilterRequest filter = FilterRequest.build().and("name", name).and(Constant.OPERATORID, operatorIds, Constant.Operator.IN);
		return count(filter) > 0;
	}
	
	default Resource getResourceByNameAndOperatorId(String name, Long operatorId) {
		if (SystemUtils.isBlank(name) || operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("name", name).and(Constant.OPERATORID, operatorId);
		return findOne(filter).orElse(null);
	}
	
	default Resource getResourceByLinkAndContentId(String link, Long contentId) {
		if (SystemUtils.isBlank(link)) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("link", link).and("contentId", contentId);
		return findOne(filter).orElse(null);
	}
	
	default Resource getResourceByContentIdAndContentType(Long contentId, Resource.ContentType contentType) {
		if (contentId == null || contentType == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("contentId", contentId).and("contentType", contentType.getValue());
		return findOne(filter).orElse(null);
	}
	
	default List<Resource> getResources(Collection<Long> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return Collections.emptyList();
		}
		FilterRequest filter = FilterRequest.build().and("id", ids, Operator.IN);
		return findList(filter);
	}
	
	default List<Resource> getResources(String excludePath, Resource.ContentType contentType, Collection<Long> operatorIds) {
		if (CollectionUtils.isEmpty(operatorIds)) {
			return Collections.emptyList();
		}
		FilterRequest filter = FilterRequest.build().and(Constant.OPERATORID, operatorIds, Operator.IN);
		if (contentType != null) {
			filter.and("contentType", contentType.getValue());
		}
		if (excludePath != null) {
			filter.and("path", excludePath, Constant.Operator.RNREGEX);
		}
		FilterSort sort = FilterSort.build("path", Direction.ASC);
		return findList(filter, sort);
	}
	
	default List<Resource> getAllChildrenResources(Long parentId, String parentPath) {
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
	@Cacheable(cacheNames="resource", key="#p0")
	default Optional<Resource> findById(Long id) {
		return BaseService.super.findById(id);
	}

	@Override
	@CacheEvict(cacheNames="resource", key="#p0.id")
	default void delete(Resource entity) {
		BaseService.super.delete(entity);
	}

	@Override
	@CachePut(cacheNames="resource", key="#p0.id")
	default Resource saveOrUpdate(Resource entity) {
		return BaseService.super.saveOrUpdate(entity);
	}

	@Override
	@CacheEvict(cacheNames="resource", key="#p0")
	default void update(Long id, Map<String, Object> updateMap) {
		BaseService.super.update(id, updateMap);
	}

	@Override
	@CacheEvict(cacheNames="resource", allEntries=true)
	default void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		BaseService.super.batchUpdate(filter, updateMap);
	}
	
	@Override
	@CacheEvict(cacheNames="resource", allEntries=true)
	default Long delete(FilterRequest filter) {
		return BaseService.super.delete(filter);
	}
	
}
