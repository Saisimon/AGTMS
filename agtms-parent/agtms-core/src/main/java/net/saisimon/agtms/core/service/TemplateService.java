package net.saisimon.agtms.core.service;

import java.util.List;
import java.util.Optional;

import org.springframework.core.Ordered;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.util.TokenUtils;

public interface TemplateService extends BaseService<Template, Long>, Ordered {
	
	default boolean removeTemplateById(Long id) {
		if (id != null && DomainGenerater.removeDomainClass(TokenUtils.getUserInfo().getLoginName(), DomainGenerater.buildGenerateName(id))) {
			delete(id);
			return true;
		}
		return false;
	}
	
	default Template getTemplate(Object id, Long userId) {
		if (id == null) {
			return null;
		}
		Optional<Template> optional = findById(Long.valueOf(id.toString()));
		if (optional.isPresent()) {
			Template template = optional.get();
			if (userId == template.getUserId()) {
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
	
	boolean exists(String title, Long userId);
	
	List<Template> getTemplates(Long navigationId, Long userId);
	
}
