package net.saisimon.agtms.web.selection;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.AuthUtils;

@Component
public class TemplateSelection extends AbstractSelection<Long> {
	
	@Override
	public LinkedHashMap<Long, String> select() {
		TemplateService templateService = TemplateServiceFactory.get();
		List<Template> templates = templateService.getTemplates(AuthUtils.getUserInfo().getUserId());
		LinkedHashMap<Long, String> templateMap = new LinkedHashMap<>(templates.size() + 1);
		templateMap.put(-1L, "");
		for (Template template : templates) {
			templateMap.put(template.getId(), template.getTitle());
		}
		return templateMap;
	}
	
}
