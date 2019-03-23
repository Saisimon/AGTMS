package net.saisimon.agtms.web.controller.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Navigation;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
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
@RequestMapping("/management/edit/{key}")
@ControllerInfo("management")
@Slf4j
public class ManagementEditController extends EditController<Domain> {
	
	@PostMapping("/grid")
	public Result grid(@PathVariable("key") String key, @RequestParam(name = "id", required = false) Long id) {
		Long userId = AuthUtils.getUserInfo().getUserId();
		Template template = TemplateUtils.getTemplate(key, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		Domain domain = null;
		if (id != null) {
			GenerateService generateService = GenerateServiceFactory.build(template);
			domain = generateService.findById(id, userId);
			if (domain == null) {
				return ErrorMessage.Domain.DOMAIN_NOT_EXIST;
			}
		}
		return ResultUtils.simpleSuccess(getEditGrid(domain, template));
	}
	
	@Operate(type=OperateTypes.EDIT)
	@Transactional
	@PostMapping("/save")
	public Result save(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		Long userId = AuthUtils.getUserInfo().getUserId();
		Template template = TemplateUtils.getTemplate(key, userId);
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
				fieldValue = DomainGenerater.parseFieldValue(fieldValue, field.getFieldType());
				if (StringUtils.isEmpty(fieldValue)) {
					if (field.getRequired()) {
						return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
					}
					if (StringUtils.isNotBlank(field.getDefaultValue())) {
						fieldValue = field.getDefaultValue();
						fieldValue = DomainGenerater.parseFieldValue(fieldValue, field.getFieldType());
					}
				}
				if (fieldValue != null) {
					domain.setField(fieldName, fieldValue, fieldValue.getClass());
				}
			}
			if (idObj == null) {
				if (generateService.checkExist(domain, userId)) {
					return ErrorMessage.Domain.DOMAIN_ALREADY_EXISTS;
				}
				generateService.saveDomain(domain);
			} else {
				Long id = Long.valueOf(idObj.toString());
				Domain oldDomain = generateService.findById(id, userId);
				if (oldDomain == null) {
					return ErrorMessage.Domain.DOMAIN_NOT_EXIST;
				}
				if (generateService.checkExist(domain, userId)) {
					return ErrorMessage.Domain.DOMAIN_ALREADY_EXISTS;
				}
				generateService.updateDomain(domain, oldDomain);
			}
			return ResultUtils.simpleSuccess();
		} catch (GenerateException e) {
			log.error("Domain save failed", e);
			return ErrorMessage.Domain.DOMAIN_SAVE_FAILED;
		}
	}
	
	@Override
	protected List<Breadcrumb> breadcrumbs(Domain domain, Object key) {
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(template.getTitle()).to("/management/main/" + template.sign()).build());
		Long nid = template.getNavigationId();
		if (nid != null && nid != -1) {
			NavigationService navigationService = NavigationServiceFactory.get();
			Map<Long, Navigation> navigationMap = navigationService.getNavigationMap(AuthUtils.getUserInfo().getUserId());
			do {
				Navigation navigation = navigationMap.get(nid);
				breadcrumbs.add(0, Breadcrumb.builder().text(navigation.getTitle()).to("/").build());
				nid = navigation.getParentId();
			} while (nid != null && nid != -1);
		}
		if (domain == null) {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("create")).active(true).build());
		} else {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("edit")).active(true).build());
		}
		return breadcrumbs;
	}

	@Override
	protected List<Field<?>> fields(Domain domain, Object key) {
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		Long userId = AuthUtils.getUserInfo().getUserId();
		List<Field<?>> fields = new ArrayList<>();
		if (CollectionUtils.isEmpty(template.getColumns())) {
			return null;
		}
		for (TemplateColumn templateColumn : template.getColumns()) {
			if (CollectionUtils.isEmpty(templateColumn.getFields())) {
				continue;
			}
			for (TemplateField templateField : templateColumn.getFields()) {
				String fieldName = templateColumn.getColumnName() + templateField.getFieldName();
				Field<Object> field = Field.<Object>builder()
						.name(fieldName)
						.text(templateField.getFieldTitle())
						.type(templateField.getFieldType())
						.ordered(templateColumn.getOrdered() * 10 + templateField.getOrdered())
						.views(templateField.getViews())
						.searchable(true)
						.build();
				if (templateField.getRequired()) {
					field.setRequired(true);
				}
				Object value = domain == null ? null : domain.getField(fieldName);
				if (Views.SELECTION.getView().equals(templateField.getViews())) {
					String selectionSign = templateField.selectionSign(template.getService());
					field.setSign(selectionSign);
					List<Option<Object>> selectionOptions = SelectionUtils.getSelectionOptions(selectionSign, null, userId);
					List<Object> options = new ArrayList<>();
					for (Option<Object> option : selectionOptions) {
						options.add(option);
					}
					field.setOptions(options);
					if (value != null) {
						Set<String> values = new HashSet<>();
						values.add(value.toString());
						Map<String, String> textMap = SelectionUtils.getSelectionValueTextMap(selectionSign, userId, values);
						String text = textMap.get(value.toString());
						if (text != null) {
							value = new Option<>(value, text);
						}
					}
				}
				if (value == null) {
					field.setValue(templateField.getDefaultValue());
				} else {
					field.setValue(value);
				}
				fields.add(field);
			}
		}
		Collections.sort(fields, Field.COMPARATOR);
		return fields;
	}
	
}
