package net.saisimon.agtms.core.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.factory.TokenFactory;

/**
 * 模板服务接口
 * 
 * @author saisimon
 *
 */
public interface TemplateService extends BaseService<Template, Long>, Ordered {
	
	default Boolean exists(String title, Long operatorId) {
		Assert.notNull(title, "title can not be null");
		Assert.notNull(operatorId, "operatorId can not be null");
		FilterRequest filter = FilterRequest.build().and("title", title).and(Constant.OPERATORID, operatorId);
		return count(filter) > 0;
	}
	
	default List<Template> getTemplates(Long operatorId) {
		if (operatorId == null) {
			return null;
		}
		UserToken userToken = TokenFactory.get().getToken(operatorId, false);
		FilterRequest filter = FilterRequest.build();
		if (userToken == null || !userToken.isAdmin()) {
			filter.and(Constant.OPERATORID, operatorId);
		}
		return findList(filter);
	}
	
	default List<Template> getTemplates(Long navigationId, Long operatorId) {
		if (navigationId == null || operatorId == null) {
			return null;
		}
		UserToken userToken = TokenFactory.get().getToken(operatorId, false);
		FilterRequest filter = FilterRequest.build().and("navigationId", navigationId);
		if (!userToken.isAdmin()) {
			filter.and(Constant.OPERATORID, operatorId);
		}
		return findList(filter);
	}
	
	default boolean createTable(Template template) {
		if (template == null) {
			return false;
		}
		return true;
	}
	
	default boolean alterTable(Template template, Template oldTemplate) {
		if (template == null || oldTemplate == null) {
			return false;
		}
		return true;
	}
	
	default boolean dropTable(Template template) {
		if (template == null) {
			return false;
		}
		return true;
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
