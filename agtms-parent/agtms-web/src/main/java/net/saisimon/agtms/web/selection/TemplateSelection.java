package net.saisimon.agtms.web.selection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.selection.AbstractSelection;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 模板下拉列表
 * 
 * @author saisimon
 *
 */
@Component
public class TemplateSelection extends AbstractSelection<String> {
	
	@Autowired
	private PremissionService premissionService;
	
	@Override
	public Map<String, String> select() {
		TemplateService templateService = TemplateServiceFactory.get();
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		List<Template> templates = templateService.getTemplates(userIds);
		Map<String, String> templateMap = MapUtil.newHashMap(templates.size() + 1, true);
		templateMap.put("-1", "");
		for (Template template : templates) {
			templateMap.put(template.getId().toString(), template.getTitle());
		}
		return templateMap;
	}
	
}
