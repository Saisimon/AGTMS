package net.saisimon.agtms.web.selection;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.AuthUtils;

@Component
public class TemplateSelection extends AbstractSelection<String> {
	
	@Override
	public Map<String, String> select() {
		TemplateService templateService = TemplateServiceFactory.get();
		List<Template> templates = templateService.getTemplates(AuthUtils.getUid());
		Map<String, String> templateMap = MapUtil.newHashMap(templates.size() + 1, true);
		templateMap.put("-1", "");
		for (Template template : templates) {
			templateMap.put(template.getId().toString(), template.getTitle());
		}
		return templateMap;
	}
	
}
