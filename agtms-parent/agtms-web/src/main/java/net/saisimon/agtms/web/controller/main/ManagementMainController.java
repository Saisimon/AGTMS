package net.saisimon.agtms.web.controller.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.domain.Task;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.Filter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.TextFilter;
import net.saisimon.agtms.core.domain.grid.BatchGrid.BatchEdit;
import net.saisimon.agtms.core.domain.grid.BatchGrid.BatchExport;
import net.saisimon.agtms.core.domain.grid.BatchGrid.BatchImport;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.grid.MainGrid.Action;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.domain.tag.Select;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.factory.TaskServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.service.TaskService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.MainController;
import net.saisimon.agtms.web.dto.req.ExportParam;
import net.saisimon.agtms.web.dto.req.ImportParam;
import net.saisimon.agtms.web.selection.FileTypeSelection;

@RestController
@RequestMapping("/management/main/{mid}")
public class ManagementMainController extends MainController {
	
	@Autowired
	private FileTypeSelection fileTypeSelection;
	
	@PostMapping("/grid")
	public Result grid(@PathVariable("mid") Long mid) {
		return ResultUtils.simpleSuccess(getMainGrid(mid));
	}
	
	@PostMapping("/list")
	public Result list(@PathVariable("mid") Long mid, @RequestParam Map<String, Object> param, @RequestBody Map<String, Object> body) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(mid, AuthUtils.getUserInfo().getUserId());
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		FilterRequest filter = FilterRequest.build(body, TemplateUtils.getFilters(template));
		FilterPageable pageable = FilterPageable.build(param);
		GenerateService generateService = GenerateServiceFactory.build(template);
		Page<Domain> page = generateService.findPage(filter, pageable);
		request.getSession().setAttribute(mid + "_filters", body);
		request.getSession().setAttribute(mid + "_pageable", param);
		return ResultUtils.pageSuccess(page.getContent(), page.getTotalElements());
	}
	
	@PostMapping("/remove")
	public Result remove(@PathVariable("mid") Long mid, @RequestParam(name = "id") Long id) {
		long userId = AuthUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(mid, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.REMOVE)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		GenerateService generateService = GenerateServiceFactory.build(template);
		generateService.delete(id);
		return ResultUtils.simpleSuccess();
	}
	
	@PostMapping("/batch/grid")
	public Result batchGrid(@PathVariable("mid") Long mid) {
		return ResultUtils.simpleSuccess(getBatchGrid(mid));
	}
	
	@PostMapping("/batch/save")
	public Result batchSave(@PathVariable("mid") Long mid, @RequestBody Map<String, Object> body) {
		List<Integer> ids = SystemUtils.transformList(body.get("ids"), Integer.class);
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(mid, AuthUtils.getUserInfo().getUserId());
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.BATCH_EDIT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		Map<String, Object> map = new HashMap<>();
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		for (Map.Entry<String, Object> entry : body.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if (StringUtils.isEmpty(value)) {
				continue;
			}
			TemplateField field = fieldInfoMap.get(key);
			if (field == null || field.getUniqued()) {
				continue;
			}
			value = DomainUtils.parseFieldValue(value, field.getFieldType());
			if (value != null) {
				map.put(key, value);
			}
		}
		if (CollectionUtils.isEmpty(map)) {
			return ResultUtils.simpleSuccess();
		}
		Long userId = AuthUtils.getUserInfo().getUserId();
		GenerateService generateService = GenerateServiceFactory.build(template);
		for (Integer id : ids) {
			Domain domain = generateService.findById(id.longValue(), userId);
			if (domain == null) {
				continue;
			}
			map.put(Constant.UPDATETIME, System.currentTimeMillis());
			generateService.updateDomain(id.longValue(), map);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@PostMapping("/batch/remove")
	public Result batchRemove(@PathVariable("mid") Long mid, @RequestBody List<Long> ids) {
		long userId = AuthUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(mid, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.BATCH_REMOVE)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		GenerateService generateService = GenerateServiceFactory.build(template);
		for (Long id : ids) {
			generateService.delete(id);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@PostMapping("/batch/export")
	public Result batchExport(@PathVariable("mid") Long mid, @Validated @RequestBody ExportParam body, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		long userId = AuthUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(mid, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.EXPORT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		body.setTemplateId(template.getId());
		body.setUserId(userId);
		Task exportTask = new Task();
		exportTask.setOperatorId(userId);
		exportTask.setTaskTime(new Date());
		exportTask.setTaskType(Functions.EXPORT.getFunction());
		exportTask.setTaskParam(SystemUtils.toJson(body));
		exportTask.setHandleStatus(HandleStatuses.CREATE.getStatus());
		TaskService taskService = TaskServiceFactory.get();
		exportTask = taskService.saveOrUpdate(exportTask);
		SystemUtils.submitTask(exportTask);
		return ResultUtils.simpleSuccess();
	}
	
	@PostMapping("/batch/import")
	public Result batchImport(@PathVariable("mid") Long mid, @Validated @RequestBody ImportParam body, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		long userId = AuthUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(mid, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.IMPORT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		body.setTemplateId(template.getId());
		body.setUserId(userId);
		Task exportTask = new Task();
		exportTask.setOperatorId(userId);
		exportTask.setTaskTime(new Date());
		exportTask.setTaskType(Functions.IMPORT.getFunction());
		exportTask.setTaskParam(SystemUtils.toJson(body));
		exportTask.setHandleStatus(HandleStatuses.CREATE.getStatus());
		TaskService taskService = TaskServiceFactory.get();
		exportTask = taskService.saveOrUpdate(exportTask);
		SystemUtils.submitTask(exportTask);
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected Header header(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, AuthUtils.getUserInfo().getUserId());
		if (template == null) {
			return null;
		}
		Header header = Header.builder().title(template.getTitle()).editUrl("/template/edit?id=" + template.getId()).build();
		if (Functions.CREATE.getCode().equals(template.getFunction() & Functions.CREATE.getCode())) {
			header.setCreateUrl("/management/edit/" + template.getId());
		}
		return header;
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		Long userId = AuthUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, userId);
		if (template == null) {
			return null;
		}
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(template.getTitle()).active(true).build());
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
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, AuthUtils.getUserInfo().getUserId());
		if (template == null) {
			return null;
		}
		List<Filter> filters = new ArrayList<>();
		for (TemplateColumn column : template.getColumns()) {
			Filter filter = new Filter();
			List<String> keyValues = new ArrayList<>();
			List<String> keyTexts = new ArrayList<>();
			Map<String, FieldFilter> value = new HashMap<>();
			for (TemplateField field : column.getFields()) {
				if (!field.getFilter() || field.getHidden()) {
					continue;
				}
				String fieldName = column.getColumnName() + field.getFieldName();
				keyValues.add(fieldName);
				keyTexts.add(field.getFieldTitle());
				if (Classes.INTEGER.getName().equals(field.getFieldType()) || Classes.DOUBLE.getName().equals(field.getFieldType())) {
					value.put(fieldName, RangeFilter.rangeFilter("", field.getFieldType(), "", field.getFieldType()));
				} else if (Classes.DATE.getName().equals(field.getFieldType())) {
					value.put(fieldName, RangeFilter.rangeFilter("", field.getFieldType(), "", field.getFieldType()));
				} else if (Classes.STRING.getName().equals(field.getFieldType())) {
					value.put(fieldName, TextFilter.textFilter("", field.getFieldType(), SingleSelect.OPERATORS.get(0)));
				}
			}
			if (keyValues.isEmpty()) {
				continue;
			}
			filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, keyTexts));
			filter.setValue(value);
			filters.add(filter);
		}
		return filters;
	}
	
	@Override
	protected List<Column> columns(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, AuthUtils.getUserInfo().getUserId());
		if (template == null) {
			return null;
		}
		TemplateUtils.sort(template);
		List<Column> columns = new ArrayList<>();
		for (TemplateColumn templateColumn : template.getColumns()) {
			for (TemplateField templateField : templateColumn.getFields()) {
				if (templateField.getHidden()) {
					continue;
				}
				String fieldName = templateColumn.getColumnName() + templateField.getFieldName();
				Column column = Column.builder().field(fieldName).label(templateField.getFieldTitle()).width(templateField.getWidth()).view(templateField.getView()).build();
				if (Classes.INTEGER.getName().equals(templateField.getFieldType())) {
					column.setType("number");
				} else if (Classes.DOUBLE.getName().equals(templateField.getFieldType())) {
					column.setType("decimal");
				} else if (Classes.DATE.getName().equals(templateField.getFieldType())) {
					column.setType("date");
					column.setDateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ");
					column.setDateOutputFormat("YYYY-MM-DD");
				}
				if (templateField.getSorted()) {
					column.setSortable(true);
					column.setOrderBy("");
				}
				columns.add(column);
			}
		}
		if (TemplateUtils.hasOneOfFunctions(template, Functions.EDIT, Functions.REMOVE)) {
			columns.add(Column.builder().field("action").label(getMessage("actions")).type("number").width(100).build());
		}
		return columns;
	}
	
	@Override
	protected List<Action> actions(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, AuthUtils.getUserInfo().getUserId());
		if (template == null) {
			return null;
		}
		List<Action> actions = new ArrayList<>();
		if (TemplateUtils.hasFunction(template, Functions.EDIT)) {
			actions.add(Action.builder().key("edit").to("/management/edit/" + key + "?id=").icon("edit").text(getMessage("edit")).type("link").build());
		}
		if (TemplateUtils.hasFunction(template, Functions.REMOVE)) {
			actions.add(Action.builder().key("remove").to("/management/main/remove").icon("trash").text(getMessage("remove")).variant("outline-danger").type("modal").build());
		}
		return actions;
	}

	@Override
	protected List<String> functions(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, AuthUtils.getUserInfo().getUserId());
		return TemplateUtils.getFunctions(template);
	}
	
	@Override
	protected BatchEdit batchEdit(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, AuthUtils.getUserInfo().getUserId());
		if (!TemplateUtils.hasFunction(template, Functions.BATCH_EDIT)) {
			return null;
		}
		BatchEdit batchEdit = new BatchEdit();
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		List<Option<String>> editFieldOptions = new ArrayList<>(fieldInfoMap.size());
		Map<String, Field<?>> editFields = new HashMap<>();
		for (Map.Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldName = entry.getKey();
			TemplateField templateField = entry.getValue();
			editFieldOptions.add(new Option<>(fieldName, templateField.getFieldTitle(), templateField.getUniqued()));
			Field<Object> field = Field.builder().name(fieldName).text(templateField.getFieldTitle()).view(templateField.getView()).type(templateField.getFieldType()).build();
			if (templateField.getRequired()) {
				field.setRequired(true);
			}
			if (StringUtils.isNotEmpty(templateField.getDefaultValue())) {
				field.setValue(templateField.getDefaultValue());
			}
			editFields.put(fieldName, field);
		}
		batchEdit.setEditFieldOptions(editFieldOptions);
		batchEdit.setEditFields(editFields);
		return batchEdit;
	}

	@Override
	protected BatchExport batchExport(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, AuthUtils.getUserInfo().getUserId());
		if (!TemplateUtils.hasFunction(template, Functions.EXPORT)) {
			return null;
		}
		BatchExport batchExport = new BatchExport();
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		List<Option<String>> exportFieldOptions = new ArrayList<>(fieldInfoMap.size());
		for (Map.Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldName = entry.getKey();
			TemplateField templateField = entry.getValue();
			exportFieldOptions.add(new Option<>(fieldName, templateField.getFieldTitle()));
		}
		batchExport.setExportFieldOptions(exportFieldOptions);
		batchExport.setExportFileTypeOptions(Select.buildOptions(fileTypeSelection.select()));
		return batchExport;
	}

	@Override
	protected BatchImport batchImport(Object key) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(key, AuthUtils.getUserInfo().getUserId());
		if (!TemplateUtils.hasFunction(template, Functions.IMPORT)) {
			return null;
		}
		BatchImport batchImport = new BatchImport();
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		List<Option<String>> importFieldOptions = new ArrayList<>(fieldInfoMap.size());
		for (Map.Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldName = entry.getKey();
			TemplateField templateField = entry.getValue();
			importFieldOptions.add(new Option<>(fieldName, templateField.getFieldTitle()));
		}
		batchImport.setImportFieldOptions(importFieldOptions);
		batchImport.setImportFileTypeOptions(Select.buildOptions(fileTypeSelection.select()));
		return batchImport;
	}
	
}
