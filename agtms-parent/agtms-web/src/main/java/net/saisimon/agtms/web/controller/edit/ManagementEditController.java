package net.saisimon.agtms.web.controller.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.EditController;

/**
 * 自定义对象管理编辑控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/management/edit/{mid}")
@ControllerInfo("management")
public class ManagementEditController extends EditController {
	
	@PostMapping("/grid")
	public Result grid(@PathVariable("mid") Long mid, @RequestParam(name = "id", required = false) Long id) {
		return ResultUtils.simpleSuccess(getEditGrid(id, mid));
	}
	
	@Operate(type=OperateTypes.EDIT)
	@Transactional
	@PostMapping("/save")
	public Result save(@PathVariable("mid") Long mid, @RequestBody Map<String, Object> body) {
		Long userId = AuthUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(mid, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		Object idObj = body.get("id");
		if (idObj == null && !TemplateUtils.hasFunction(template, Functions.CREATE)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		} else if (idObj != null && !TemplateUtils.hasFunction(template, Functions.EDIT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		try {
			GenerateService generateService = GenerateServiceFactory.build(template);
			Domain domain = generateService.newGenerate();
			Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
			for (Map.Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
				String fieldName = entry.getKey();
				TemplateField field = entry.getValue();
				Object fieldValue = body.get(fieldName);
				fieldValue = DomainUtils.parseFieldValue(fieldValue, field.getFieldType());
				if (StringUtils.isEmpty(fieldValue)) {
					if (field.getRequired()) {
						return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
					}
					if (StringUtils.isNotBlank(field.getDefaultValue())) {
						fieldValue = field.getDefaultValue();
						fieldValue = DomainUtils.parseFieldValue(fieldValue, field.getFieldType());
					}
				}
				if (fieldValue != null) {
					domain.setField(fieldName, fieldValue, fieldValue.getClass());
				}
			}
			if (idObj == null) {
				if (generateService.checkExist(domain)) {
					return ErrorMessage.Domain.DOMAIN_ALREADY_EXISTS;
				}
				generateService.saveDomain(domain);
			} else {
				Long id = Long.valueOf(idObj.toString());
				Domain oldDomain = generateService.findById(id, userId);
				if (oldDomain == null) {
					return ErrorMessage.Domain.DOMAIN_NOT_EXIST;
				}
				generateService.updateDomain(domain, oldDomain);
			}
			return ResultUtils.simpleSuccess();
		} catch (GenerateException e) {
			return ErrorMessage.Domain.DOMAIN_SAVE_FAILED;
		}
	}
	
	@Override
	protected List<Breadcrumb> breadcrumbs(Long id, Object key) {
		Long userId = AuthUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, userId);
		if (template == null) {
			return null;
		}
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(template.getTitle()).to("/management/main/" + key).build());
		Long nid = template.getNavigationId();
		if (nid != null && nid != -1) {
			NavigationService navigationService = NavigationServiceFactory.get();
			Map<Long, Navigation> navigationMap = navigationService.getNavigationMap(userId);
			do {
				Navigation navigation = navigationMap.get(nid);
				breadcrumbs.add(0, Breadcrumb.builder().text(navigation.getTitle()).to("/").build());
				nid = navigation.getParentId();
			} while (nid != null && nid != -1);
		}
		if (id == null) {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("create")).active(true).build());
		} else {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("edit")).active(true).build());
		}
		return breadcrumbs;
	}

	@Override
	protected List<Field<?>> fields(Long id, Object key) {
		Long userId = AuthUtils.getUserInfo().getUserId();
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
		for (TemplateColumn templateColumn : template.getColumns()) {
			for (TemplateField templateField : templateColumn.getFields()) {
				String fieldName = templateColumn.getColumnName() + templateField.getFieldName();
				Field<Object> field = Field.builder()
						.name(fieldName)
						.text(templateField.getFieldTitle())
						.type(templateField.getFieldType())
						.ordered(templateColumn.getOrdered() * 10 + templateField.getOrdered())
						.view(templateField.getView())
						.build();
				if (templateField.getRequired()) {
					field.setRequired(true);
				}
				if (domain != null) {
					field.setValue(domain.getField(fieldName));
				} else {
					field.setValue(templateField.getDefaultValue());
				}
				fields.add(field);
			}
		}
		Collections.sort(fields, (f1, f2) -> {
			if (f1.getOrdered() == null) {
				return -1;
			}
			if (f2.getOrdered() == null) {
				return 1;
			}
			return f1.getOrdered().compareTo(f2.getOrdered());
		});
		return fields;
	}

}
