package net.saisimon.agtms.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.cache.Cache;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.factory.CacheFactory;
import net.saisimon.agtms.core.generate.DomainGenerater;

/**
 * 模版服务接口
 * 
 * @author saisimon
 *
 */
public interface TemplateService extends BaseService<Template, Long>, Ordered {
	
	public static final String TEMPLATE_KEY = "template_%s";
	public static final long TEMPLATE_TIMEOUT = 60 * 1000L;
	
	default boolean removeTemplate(Template template) {
		if (template == null || template.getId() == null || template.getOperatorId() == null) {
			return false;
		}
		if (DomainGenerater.removeDomainClass(template.getOperatorId().toString(), DomainGenerater.buildGenerateName(template.getId()))) {
			delete(template.getId());
			Cache cache = CacheFactory.get();
			cache.delete(String.format(TEMPLATE_KEY, template.getId()));
			return true;
		}
		return false;
	}
	
	default Template getTemplate(Object id, Long operatorId) {
		if (id == null || operatorId == null) {
			return null;
		}
		String key = String.format(TEMPLATE_KEY, id.toString());
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
	
	default void createTable(Template template) {}
	
	default void alterTable(Template template, Template oldTemplate) {}
	
	default void dropTable(Template template) {}
	
	boolean exists(String title, Long operatorId);
	
	List<Template> getTemplates(Long navigationId, Long operatorId);

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
