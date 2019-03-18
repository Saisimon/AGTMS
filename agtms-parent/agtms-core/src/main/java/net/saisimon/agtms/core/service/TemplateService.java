package net.saisimon.agtms.core.service;

import java.util.List;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.cache.Cache;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.factory.CacheFactory;
import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.util.TemplateUtils;

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
	
	default List<Template> getTemplates(Long operatorId) {
		if (operatorId == null) {
			return null;
		}
		FilterRequest filter = FilterRequest.build().and("operatorId", operatorId);
		return findList(filter);
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
		if (entity == null) {
			return;
		}
		Cache cache = CacheFactory.get();
		cache.delete(String.format(TEMPLATE_KEY, entity.getId()));
		DomainGenerater.removeDomainClass(TemplateUtils.getNamespace(entity), DomainGenerater.buildGenerateName(entity.sign()));
		BaseService.super.delete(entity);
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
