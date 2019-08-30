package net.saisimon.agtms.core.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.filter.FilterRequest;

/**
 * 模板服务接口
 * 
 * @author saisimon
 *
 */
public interface TemplateService extends BaseService<Template, Long>, Ordered {
	
	default Boolean exists(String title, Collection<Long> operatorIds) {
		Assert.notNull(title, "title can not be null");
		Assert.notNull(operatorIds, "operatorId can not be null");
		FilterRequest filter = FilterRequest.build().and("title", title).and(Constant.OPERATORID, operatorIds, Constant.Operator.IN);
		return count(filter) > 0;
	}
	
	default List<Template> getTemplates(Collection<Long> operatorIds) {
		if (CollectionUtils.isEmpty(operatorIds)) {
			return Collections.emptyList();
		}
		FilterRequest filter = FilterRequest.build().and(Constant.OPERATORID, operatorIds, Constant.Operator.IN);
		return findList(filter);
	}
	
	@Override
	@Cacheable(cacheNames="template", key="#p0")
	default Optional<Template> findById(Long id) {
		return BaseService.super.findById(id);
	}
	
	@Override
	@CacheEvict(cacheNames="template", key="#p0.id")
	default void delete(Template entity) {
		BaseService.super.delete(entity);
	}
	
	@Override
	@CacheEvict(cacheNames="template", key="#p0.id")
	default Template saveOrUpdate(Template entity) {
		return BaseService.super.saveOrUpdate(entity);
	}
	
	@Override
	@CacheEvict(cacheNames="template", key="#p0")
	default void update(Long id, Map<String, Object> updateMap) {
		BaseService.super.update(id, updateMap);
	}
	
	@Override
	@CacheEvict(cacheNames="template", allEntries=true)
	default void batchUpdate(FilterRequest filter, Map<String, Object> updateMap) {
		BaseService.super.batchUpdate(filter, updateMap);
	}

	@Override
	@CacheEvict(cacheNames="template", allEntries=true)
	default Long delete(FilterRequest filter) {
		return BaseService.super.delete(filter);
	}

}
