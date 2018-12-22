package net.saisimon.agtms.web.controller.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.EditGrid.Field;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.TokenUtils;
import net.saisimon.agtms.web.controller.base.EditController;
import net.saisimon.agtms.web.dto.resp.common.Result;
import net.saisimon.agtms.web.util.ResultUtils;

@RestController
@RequestMapping("/manage/edit/{mid}")
public class ManagementEditController extends EditController {
	
	@PostMapping("/grid")
	public Result grid(@PathVariable("mid") Long mid, @RequestParam(name = "id", required = false) Long id) {
		return ResultUtils.success(getEditGrid(id, mid));
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		Long userId = TokenUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, userId);
		if (template == null) {
			return null;
		}
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(template.getTitle()).to("/manage/main/" + key).build());
		Long nid = template.getNavigationId();
		if (nid != null && nid != -1) {
			NavigationService navigationService = NavigationServiceFactory.get();
			Map<Long, Navigation> navigateMap = navigationService.getNavigationMap(userId);
			do {
				Navigation navigation = navigateMap.get(nid);
				breadcrumbs.add(0, Breadcrumb.builder().text(navigation.getTitle()).to("/").build());
				nid = navigation.getParentId();
			} while (nid != null && nid != -1);
		}
		return breadcrumbs;
	}

	@Override
	protected List<Field<?>> fields(Long id, Object key) {
		Long userId = TokenUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, userId);
		if (template == null) {
			return null;
		}
		Domain domain = null;
		if (id != null) {
			GenerateService generateService = GenerateServiceFactory.build(template);
			domain = generateService.findById(id, userId);
		}
		List<Field<?>> fields = new ArrayList<>();
		for (TemplateColumn pageColumn : template.getColumns()) {
			for (TemplateField pageField : pageColumn.getFields()) {
				String fieldName = pageColumn.getColumnName() + pageField.getFieldName();
				Field<Object> field = Field.builder().name(fieldName).text(pageField.getFieldTitle()).type(pageField.getFieldType()).view(pageField.getView()).build();
				if (pageField.getRequired()) {
					field.setRequired(true);
				}
				if (domain != null) {
					field.setValue(domain.getField(fieldName));
				} else {
					field.setValue(pageField.getDefaultValue());
				}
				fields.add(field);
			}
		}
		return fields;
	}

}
