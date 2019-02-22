package net.saisimon.agtms.core.service;

import java.util.List;
import java.util.Optional;

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
	
	default Template getTemplate(Object id, Long operatorId) {
		if (id == null || operatorId == null) {
			return null;
		}
		String key = String.format(TEMPLATE_KEY, id);
		Cache cache = CacheFactory.get();
		Template template = cache.get(key, Template.class);
		if (template == null) {
			Optional<Template> optional = findById(Long.valueOf(id.toString()));
			if (optional.isPresent()) {
				template = optional.get();
				cache.set(key, template, TEMPLATE_TIMEOUT);
			}
		}
		if (template != null && operatorId == template.getOperatorId()) {
			return template;
		}
		return null;
	}
	
	@Override
	default Template delete(Long id) {
		Template template = BaseService.super.delete(id);
		if (template != null) {
			Cache cache = CacheFactory.get();
			cache.delete(String.format(TEMPLATE_KEY, id));
			if (template.getOperatorId() != null) {
				DomainGenerater.removeDomainClass(template.getOperatorId().toString(), DomainGenerater.buildGenerateName(template.getId()));
			}
		}
		return template;
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
