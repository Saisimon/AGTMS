package net.saisimon.agtms.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.generate.DomainGenerater;

/**
 * 模版服务接口
 * 
 * @author saisimon
 *
 */
public interface TemplateService extends BaseService<Template, Long>, Ordered {
	
	default boolean removeTemplate(Template template) {
		if (template == null || template.getId() == null || template.getOperatorId() == null) {
			return false;
		}
		if (DomainGenerater.removeDomainClass(template.getOperatorId().toString(), DomainGenerater.buildGenerateName(template.getId()))) {
			delete(template.getId());
			return true;
		}
		return false;
	}
	
	default Template getTemplate(Object id, Long operatorId) {
		if (id == null || operatorId == null) {
			return null;
		}
		Optional<Template> optional = findById(Long.valueOf(id.toString()));
		if (optional.isPresent()) {
			Template template = optional.get();
			if (operatorId == template.getOperatorId()) {
				return template;
			}
		}
		return null;
	}
	
	default void createTable(Template template) {
		
	}
	
	default void alterTable(Template template, Template oldTemplate) {
		
	}
	
	default void dropTable(Template template) {
		
	}
	
	boolean exists(String title, Long operatorId);
	
	List<Template> getTemplates(Long navigationId, Long operatorId);
	
}
