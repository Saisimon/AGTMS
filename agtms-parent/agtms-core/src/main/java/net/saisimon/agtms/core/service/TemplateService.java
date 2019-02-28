package net.saisimon.agtms.core.service;

import java.util.List;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.cache.Cache;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.factory.CacheFactory;
import net.saisimon.agtms.core.generate.DomainGenerater;

/**
 * 模板服务接口
 * 
 * @author saisimon
 *
 */
public interface TemplateService extends BaseService<Template, Long>, Ordered {
	
	public static final String TEMPLATE_KEY = "template_%s";
	public static final long TEMPLATE_TIMEOUT = 60 * 1000L;
	
	default Boolean exists(String title, Long operatorId) {
		if (title == null || operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("title", title).and("operatorId", operatorId);
		return count(filter) > 0;
	}
	
	default List<Template> getTemplates(Long navigationId, Long operatorId) {
		if (navigationId == null || operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("navigationId", navigationId).and("operatorId", operatorId);
		return findList(filter);
	}
	
	@Override
	default void delete(Template entity) {
		Cache cache = CacheFactory.get();
		cache.delete(String.format(TEMPLATE_KEY, entity.getId()));
		if (entity.getOperatorId() != null) {
			DomainGenerater.removeDomainClass(entity.getOperatorId().toString(), DomainGenerater.buildGenerateName(entity.sign()));
		}
		BaseService.super.delete(entity);
	}

	default boolean createTable(Template template) {
		return true;
	}
	
	default boolean alterTable(Template template, Template oldTemplate) {
		return true;
	}
	
	default boolean dropTable(Template template) {
		return true;
	}
	
	@Override
	default Template saveOrUpdate(Template entity) {
		Template template = BaseService.super.saveOrUpdate(entity);
		if (template != null) {
			String key = String.format(TEMPLATE_KEY, template.getId());
			Cache cache = CacheFactory.get();
			cache.set(key, template, TEMPLATE_TIMEOUT);
		}
		return template;
	}
	
}
