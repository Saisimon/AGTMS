package net.saisimon.agtms.web.service.edit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.EditGrid.FieldGroup;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.service.base.AbstractEditService;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 自定义对象管理编辑服务
 * 
 * @author saisimon
 *
 */
@Service
@Slf4j
public class ManagementEditService extends AbstractEditService<Domain> {
	
	@Autowired
	private PremissionService premissionService;
	
	public Result grid(String key, Long id) {
		Template template = TemplateUtils.getTemplate(key);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		Domain domain = null;
		if (id != null) {
			Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
			domain = GenerateServiceFactory.build(template).findById(id, userIds);
			if (domain == null) {
				return ErrorMessage.Domain.DOMAIN_NOT_EXIST;
			}
		}
		return ResultUtils.simpleSuccess(getEditGrid(domain, template));
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result save(String key, Map<String, Object> body) {
		Long userId = AuthUtils.getUid();
		Template template = TemplateUtils.getTemplate(key);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		Object idObj = body.get(Constant.ID);
		Long id = null;
		if (idObj != null) {
			id = Long.valueOf(idObj.toString());
		}
		try {
			Domain domain = GenerateServiceFactory.build(template).newGenerate();
			Result result = populate(domain, template, body);
			if (!ResultUtils.isSuccess(result)) {
				return result;
			}
			Set<Long> userIds = premissionService.getUserIds(userId);
			if (id == null) {
				result = saveDomain(domain, template, userIds, userId);
			} else {
				result = updateDomain(domain, template, id, userIds, userId);
			}
			return result;
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
		ResourceService resourceService = ResourceServiceFactory.get();
		if (SystemUtils.isNotBlank(template.getPath())) {
			String[] strs = template.getPath().split("/");
			for (String str : strs) {
				try {
					Resource r = resourceService.findById(Long.valueOf(str)).orElse(null);
					if (r == null) {
						continue;
					}
					breadcrumbs.add(Breadcrumb.builder().text(r.getName()).to("/").build());
				} catch (NumberFormatException e) {}
			}
		}
		breadcrumbs.add(Breadcrumb.builder().text(template.getTitle()).to("/management/main/" + template.sign()).build());
		if (domain == null) {
			breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("create")).active(true).build());
		} else {
			breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("edit")).active(true).build());
		}
		return breadcrumbs;
	}

	@Override
	protected List<FieldGroup> groups(Domain domain, Object key) {
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		if (CollectionUtils.isEmpty(template.getColumns())) {
			return null;
		}
		List<FieldGroup> groups = new ArrayList<>();
		for (TemplateColumn templateColumn : template.getColumns()) {
			if (CollectionUtils.isEmpty(templateColumn.getFields())) {
				continue;
			}
			List<Field<?>> fields = new ArrayList<>();
			for (TemplateField templateField : templateColumn.getFields()) {
				fields.add(buildField(domain, templateColumn.getColumnName(), templateField, template.getService()));
			}
			Collections.sort(fields, Field.COMPARATOR);
			FieldGroup group = new FieldGroup();
			group.setFields(fields);
			group.setOrdered(templateColumn.getOrdered());
			group.setText(templateColumn.getTitle());
			groups.add(group);
		}
		Collections.sort(groups, FieldGroup.COMPARATOR);
		return groups;
	}
	
	private Field<Object> buildField(Domain domain, String columnName, TemplateField templateField, String service) {
		String fieldName = columnName + templateField.getFieldName();
		Field<Object> field = Field.<Object>builder()
				.name(fieldName)
				.text(templateField.getFieldTitle())
				.type(templateField.getFieldType())
				.ordered(templateField.getOrdered())
				.views(templateField.getViews())
				.searchable(true)
				.build();
		field.setRequired(templateField.getRequired());
		Object value = domain == null ? null : domain.getField(fieldName);
		if (Views.SELECTION.getKey().equals(templateField.getViews())) {
			String selectionSign = templateField.selectionSign(service);
			field.setSign(selectionSign);
			field.setOptions(getFieldOptions(selectionSign));
			if (value == null) {
				value = templateField.getDefaultValue();
			}
			field.setValue(value);
		} else if (Views.PASSWORD.getKey().equals(field.getViews())) {
			if (value == null) {
				value = templateField.getDefaultValue();
			}
			field.setValue(DomainUtils.decrypt(value));
		} else {
			field.setValue(value == null ? DomainUtils.parseFieldValue(templateField.getDefaultValue(), templateField.getFieldType()) : value);
		}
		return field;
	}
	
	private List<Object> getFieldOptions(String selectionSign) {
		List<Option<Object>> selectionOptions = SelectionUtils.getSelectionOptions(selectionSign, null);
		List<Object> options = new ArrayList<>();
		for (Option<Object> option : selectionOptions) {
			options.add(option);
		}
		return options;
	}
	
	private Result populate(Domain domain, Template template, Map<String, Object> body) {
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		for (Map.Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldName = entry.getKey();
			TemplateField field = entry.getValue();
			Object fieldValue = body.get(fieldName);
			fieldValue = DomainUtils.parseFieldValue(fieldValue, field.getFieldType());
			if (SystemUtils.isEmpty(fieldValue) && field.getRequired()) {
				return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
			}
			Result result = TemplateUtils.validate(template, field, fieldValue);
			if (!ResultUtils.isSuccess(result)) {
				return result;
			}
			if (SystemUtils.isEmpty(fieldValue) && SystemUtils.isNotEmpty(field.getDefaultValue())) {
				fieldValue = DomainUtils.parseFieldValue(field.getDefaultValue(), field.getFieldType());
			} else if (Views.PASSWORD.getKey().equals(field.getViews())) {
				fieldValue = DomainUtils.encrypt(fieldValue);
			}
			if (fieldValue == null) {
				continue;
			}
			domain.setField(fieldName, fieldValue, fieldValue.getClass());
		}
		return ResultUtils.simpleSuccess();
	}
	
	private Result saveDomain(Domain domain, Template template, Collection<Long> userIds, Long userId) {
		if (GenerateServiceFactory.build(template).checkExist(domain, userIds)) {
			return ErrorMessage.Domain.DOMAIN_ALREADY_EXISTS;
		}
		return GenerateServiceFactory.build(template).saveDomain(domain, userId) == null ? ErrorMessage.Domain.DOMAIN_SAVE_FAILED : ResultUtils.simpleSuccess();
	}
	
	private Result updateDomain(Domain domain, Template template, Long id, Collection<Long> userIds, Long userId) {
		Domain oldDomain = GenerateServiceFactory.build(template).findById(id, userIds);
		if (oldDomain == null) {
			return ErrorMessage.Domain.DOMAIN_NOT_EXIST;
		}
		domain.setField(Constant.ID, id, Long.class);
		if (GenerateServiceFactory.build(template).checkExist(domain, userIds)) {
			return ErrorMessage.Domain.DOMAIN_ALREADY_EXISTS;
		}
		return GenerateServiceFactory.build(template).updateDomain(domain, oldDomain, userId) == null ? ErrorMessage.Domain.DOMAIN_SAVE_FAILED : ResultUtils.simpleSuccess();
	}
	
}
