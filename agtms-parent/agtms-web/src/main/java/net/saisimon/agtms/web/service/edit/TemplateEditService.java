package net.saisimon.agtms.web.service.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.entity.RoleResource;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.entity.UserRole;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Editor;
import net.saisimon.agtms.core.domain.grid.TemplateGrid;
import net.saisimon.agtms.core.domain.grid.TemplateGrid.Column;
import net.saisimon.agtms.core.domain.grid.TemplateGrid.Table;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.domain.tag.MultipleSelect;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.domain.tag.Select;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.EditorTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleResourceServiceFactory;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.factory.UserRoleServiceFactory;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.core.service.RoleResourceService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.selection.ClassSelection;
import net.saisimon.agtms.web.selection.DataSourceSelection;
import net.saisimon.agtms.web.selection.FunctionSelection;
import net.saisimon.agtms.web.selection.ResourceSelection;
import net.saisimon.agtms.web.selection.ViewSelection;
import net.saisimon.agtms.web.selection.WhetherSelection;
import net.saisimon.agtms.web.service.common.MessageService;
import net.saisimon.agtms.web.service.common.PremissionService;
import net.saisimon.agtms.web.service.main.ManagementMainService;

/**
 * 模板编辑服务
 * 
 * @author saisimon
 *
 */
@Service
public class TemplateEditService {
	
	@Autowired
	private ClassSelection classSelection;
	@Autowired
	private WhetherSelection whetherSelection;
	@Autowired
	private ViewSelection viewSelection;
	@Autowired
	private FunctionSelection functionSelection;
	@Autowired
	private ResourceSelection resourceSelection;
	@Autowired
	private DataSourceSelection dataSourceSelection;
	@Autowired
	private MessageService messageService;
	@Autowired
	private PremissionService premissionService;
	@Autowired
	private NavigationEditService navigationEditService;
	
	public Result grid(Long id) {
		Template template = null;
		if (id != null) {
			template = TemplateUtils.getTemplate(id);
			if (template == null) {
				return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
			}
		}
		Long userId = AuthUtils.getUid();
		Set<Long> userIds = premissionService.getUserIds(userId);
		TemplateGrid grid = new TemplateGrid();
		grid.setBreadcrumbs(breadcrumbs(id));
		grid.setClassOptions(Select.buildOptions(classSelection.select()));
		grid.setViewOptions(Select.buildOptions(viewSelection.select()));
		grid.setWhetherOptions(Select.buildOptions(whetherSelection.select()));
		grid.setSelectionOptions(Select.buildOptions(SelectionServiceFactory.get().getSelectionMap(userIds)));
		grid.setTable(buildTable(grid, template));
		MultipleSelect<Integer> functionSelect = new MultipleSelect<>();
		functionSelect.setOptions(Select.buildOptions(functionSelection.select(ManagementMainService.SUPPORT_FUNCTIONS)));
		SingleSelect<String> navigationSelect = new SingleSelect<>();
		navigationSelect.setOptions(resourceSelection.buildNestedOptions(null, Resource.ContentType.NAVIGATION, false));
		SingleSelect<String> dataSourceSelect = new SingleSelect<>();
		dataSourceSelect.setOptions(Select.buildOptions(dataSourceSelection.select()));
		if (template != null) {
			grid.setTitle(new Editor<>(template.getTitle(), "title"));
			functionSelect.setSelected(getFunctions(template, userId));
			navigationSelect.setSelected(SystemUtils.isEmpty(template.getPath()) ? null : template.getPath());
			dataSourceSelect.setSelected(template.getSource());
		} else {
			grid.setTitle(new Editor<>("", "title"));
			functionSelect.setSelected(Collections.emptyList());
			dataSourceSelect.setSelected(dataSourceSelect.getOptions().get(0).getId());
		}
		grid.setNavigationSelect(navigationSelect);
		grid.setFunctionSelect(functionSelect);
		grid.setDataSourceSelect(dataSourceSelect);
		return ResultUtils.simpleSuccess(grid);
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result save(Template template) throws GenerateException {
		List<Sign> signs = GenerateServiceFactory.getSigns();
		if (CollectionUtils.isEmpty(signs)) {
			return ErrorMessage.Common.SERVER_ERROR;
		}
		if (!TemplateUtils.checkRequired(template)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		if (template.getTitle().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("title"), 32);
		}
		if (template.getPath() == null) {
			template.setPath("");
		}
		Long userId = AuthUtils.getUid();
		Set<Long> userIds = premissionService.getUserIds(userId);
		if (!navigationEditService.validatePath(template.getPath(), null, userIds)) {
			return ErrorMessage.Common.PARAM_ERROR;
		}
		TemplateService templateService = TemplateServiceFactory.get();
		if (template.getId() != null) {
			Template oldTemplate = TemplateUtils.getTemplate(template.getId());
			if (oldTemplate == null) {
				return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
			}
			if (!template.getTitle().equals(oldTemplate.getTitle()) && templateService.exists(template.getTitle(), userIds)) {
				return ErrorMessage.Template.TEMPLATE_ALREADY_EXISTS;
			}
			if (!GenerateServiceFactory.build(template).alterTable(oldTemplate)) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return ErrorMessage.Template.TEMPLATE_MODIFY_FAILED;
			}
			updateOldTemplate(template, oldTemplate);
			templateService.saveOrUpdate(oldTemplate);
			updateOldResource(template, oldTemplate, userId);
		} else {
			if (templateService.exists(template.getTitle(), userIds)) {
				return ErrorMessage.Template.TEMPLATE_ALREADY_EXISTS;
			}
			checkSource(template, signs);
			modifyNewTemplate(template, userId);
			templateService.saveOrUpdate(template);
			saveNewResource(template, userId);
			if (!GenerateServiceFactory.build(template).createTable()) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return ErrorMessage.Template.TEMPLATE_CREATE_FAILED;
			}
		}
		return ResultUtils.simpleSuccess();
	}
	
	public Integer getFunction(Template template, Long userId) {
		if (template == null || template.getId() == null) {
			return null;
		}
		Resource resource = ResourceServiceFactory.get().getResourceByContentIdAndContentType(template.getId(), Resource.ContentType.TEMPLATE);
		if (resource == null) {
			return null;
		}
		Map<String, Integer> roleResourceMap = premissionService.getRoleResourceMap(userId);
		if (CollectionUtils.isEmpty(roleResourceMap)) {
			return null;
		}
		return roleResourceMap.get(resource.getId().toString());
	}
	
	private void updateOldResource(Template template, Template oldTemplate, Long userId) {
		ResourceService resourceService = ResourceServiceFactory.get();
		Resource oldResource = resourceService.getResourceByContentIdAndContentType(oldTemplate.getId(), Resource.ContentType.TEMPLATE);
		oldResource.setName(template.getTitle());
		oldResource.setPath(template.getPath());
		oldResource.setUpdateTime(new Date());
		resourceService.saveOrUpdate(oldResource);
		List<UserRole> userRoles = UserRoleServiceFactory.get().getUserRoles(userId);
		if (!CollectionUtils.isEmpty(userRoles)) {
			RoleResourceService roleResourceService = RoleResourceServiceFactory.get();
			List<RoleResource> roleResources = roleResourceService.getRoleResources(userRoles.parallelStream().map(UserRole::getRoleId).collect(Collectors.toList()), oldResource.getId());
			if (!CollectionUtils.isEmpty(roleResources)) {
				for (RoleResource roleResource : roleResources) {
					roleResource.setResourceFunctions(template.getFunctions());
					roleResourceService.saveOrUpdate(roleResource);
				}
			}
		}
	}

	private void modifyNewTemplate(Template template, Long userId) {
		Date time = new Date();
		template.setCreateTime(time);
		template.setOperatorId(userId);
		template.setUpdateTime(time);
		for (TemplateColumn column : template.getColumns()) {
			for (TemplateField field : column.getFields()) {
				if (Views.PASSWORD.getKey().equals(field.getViews()) && SystemUtils.isNotEmpty(field.getDefaultValue())) {
					Object value = DomainUtils.encrypt(field.getDefaultValue());
					field.setDefaultValue(value == null ? null : value.toString());
				}
				column.addField(field);
			}
			template.addColumn(column);
		}
	}

	private void updateOldTemplate(Template template, Template oldTemplate) {
		oldTemplate.setColumnIndex(template.getColumnIndex());
		oldTemplate.setTitle(template.getTitle());
		oldTemplate.setPath(template.getPath());
		oldTemplate.setUpdateTime(new Date());
		Map<String, TemplateColumn> oldColumnMap = oldTemplate.getColumns().stream().collect(Collectors.toMap(TemplateColumn::getColumnName, c -> c));
		for (TemplateColumn column : template.getColumns()) {
			if (oldColumnMap.containsKey(column.getColumnName())) {
				TemplateColumn oldTemplateColumn = oldColumnMap.remove(column.getColumnName());
				Map<String, TemplateField> oldFieldMap = oldTemplateColumn.getFields().stream().collect(Collectors.toMap(TemplateField::getFieldName, f -> f));
				for (TemplateField field : column.getFields()) {
					if (oldFieldMap.containsKey(field.getFieldName())) {
						TemplateField oldField = oldFieldMap.remove(field.getFieldName());
						field.setId(oldField.getId());
						oldTemplateColumn.removeField(oldField);
					}
					if (Views.PASSWORD.getKey().equals(field.getViews()) && SystemUtils.isNotEmpty(field.getDefaultValue())) {
						Object value = DomainUtils.encrypt(field.getDefaultValue());
						field.setDefaultValue(value == null ? null : value.toString());
					}
					oldTemplateColumn.addField(field);
				}
				for (TemplateField oldField : oldFieldMap.values()) {
					oldTemplateColumn.removeField(oldField);
				}
				column.setId(oldTemplateColumn.getId());
				oldTemplate.removeColumn(oldTemplateColumn);
			} else {
				for (TemplateField field : column.getFields()) {
					if (Views.PASSWORD.getKey().equals(field.getViews()) && SystemUtils.isNotEmpty(field.getDefaultValue())) {
						Object value = DomainUtils.encrypt(field.getDefaultValue());
						field.setDefaultValue(value == null ? null : value.toString());
					}
					column.addField(field);
				}
			}
			oldTemplate.addColumn(column);
		}
		for (TemplateColumn oldColumn : oldColumnMap.values()) {
			oldTemplate.removeColumn(oldColumn);
		}
	}
	
	private void saveNewResource(Template template, Long userId) {
		Resource newResource = new Resource();
		newResource.setContentId(template.getId());
		newResource.setContentType(Resource.ContentType.TEMPLATE.getValue());
		Date time = new Date();
		newResource.setCreateTime(time);
		newResource.setIcon("link");
		newResource.setLink("/management/main");
		newResource.setName(template.getTitle());
		newResource.setOperatorId(userId);
		newResource.setPath(template.getPath());
		newResource.setUpdateTime(time);
		ResourceServiceFactory.get().saveOrUpdate(newResource);
		List<UserRole> userRoles = UserRoleServiceFactory.get().getUserRoles(userId);
		if (!CollectionUtils.isEmpty(userRoles)) {
			RoleResourceService roleResourceService = RoleResourceServiceFactory.get();
			for (UserRole userRole : userRoles) {
				RoleResource roleResource = new RoleResource();
				roleResource.setResourceId(newResource.getId());
				roleResource.setResourcePath(newResource.getPath());
				roleResource.setResourceFunctions(template.getFunctions());
				roleResource.setRoleId(userRole.getRoleId());
				roleResourceService.saveOrUpdate(roleResource);
			}
		}
	}

	private void checkSource(Template template, List<Sign> signs) {
		String source = template.getSource();
		boolean check = false;
		for (Sign sign : signs) {
			if (sign.getName().equals("remote")) {
				continue;
			}
			if (sign.getName().equalsIgnoreCase(source)) {
				source = sign.getName();
				check = true;
			}
		}
		if (!check) {
			source = signs.get(0).getName();
		}
		template.setSource(source);
	}

	private Table buildTable(TemplateGrid grid, Template template) {
		Table table = new Table();
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("title").width(100).build());
		List<Map<String, Object>> rows = new ArrayList<>();
		Map<String, Object> columnNameRow = TemplateGrid.buildRow("columnName", messageService.getMessage("column.name"), EditorTypes.INPUT);
		columnNameRow.put("required", true);
		Map<String, Object> fieldRow = TemplateGrid.buildRow("field", "", EditorTypes.TABLE);
		Map<String, Object> removeRow = TemplateGrid.buildRow("remove", "-", EditorTypes.REMOVE);
		rows.add(columnNameRow);
		rows.add(fieldRow);
		rows.add(removeRow);
		Integer idx = 1;
		if (template != null) {
			idx = template.getColumnIndex();
			for (TemplateColumn column : template.getColumns()) {
				columns.add(Column.builder().field(column.getColumnName()).ordered(column.getOrdered()).build());
				columnNameRow.put(column.getColumnName(), new Editor<>(column.getTitle(), column.getColumnName()));
				fieldRow.put(column.getColumnName(), buildSubTable(grid, column, template.getService()));
				removeRow.put(column.getColumnName(), "");
			}
		} else {
			String columnName = "column0";
			columns.add(Column.builder().field(columnName).ordered(0).build());
			columnNameRow.put(columnName, new Editor<>("", columnName));
			fieldRow.put(columnName, buildSubTable(grid, null, null));
			removeRow.put(columnName, "");
		}
		table.setIdx(idx);
		table.setRows(rows);
		Collections.sort(columns, (c1, c2) -> {
			if (c1.getOrdered() == null) {
				return -1;
			}
			if (c2.getOrdered() == null) {
				return 1;
			}
			return c1.getOrdered().compareTo(c2.getOrdered());
		});
		table.setColumns(columns);
		return table;
	}
	
	private Table buildSubTable(TemplateGrid grid, TemplateColumn column, String service) {
		Table subTable = new Table();
		List<Column> subColumns = new ArrayList<>();
		List<Map<String, Object>> subRows = new ArrayList<>();
		Map<String, Object> fieldNameRow = TemplateGrid.buildRow("fieldName", null, EditorTypes.INPUT);
		Map<String, Object> fieldTypeRow = TemplateGrid.buildRow("fieldType", null, EditorTypes.SELECT);
		Map<String, Object> showTypeRow = TemplateGrid.buildRow("showType", null, EditorTypes.SELECT);
		Map<String, Object> filterRow = TemplateGrid.buildRow("filtered", null, EditorTypes.SELECT);
		Map<String, Object> sortedRow = TemplateGrid.buildRow("sorted", null, EditorTypes.SELECT);
		Map<String, Object> requiredRow = TemplateGrid.buildRow("required", null, EditorTypes.SELECT);
		Map<String, Object> uniquedRow = TemplateGrid.buildRow("uniqued", null, EditorTypes.SELECT);
		Map<String, Object> hiddenRow = TemplateGrid.buildRow("hidden", null, EditorTypes.SELECT);
		Map<String, Object> subremoveRow = TemplateGrid.buildRow("remove", null, EditorTypes.REMOVE);
		Map<String, Object> defaultRow = TemplateGrid.buildRow("default", null, EditorTypes.INPUT);
		Integer idx = 1;
		if (column != null) {
			idx = column.getFieldIndex();
			for (TemplateField field : column.getFields()) {
				subColumns.add(Column.builder().field(field.getFieldName()).ordered(field.getOrdered()).build());
				fieldNameRow.put(field.getFieldName(), new Editor<>(field.getFieldTitle(), field.getFieldName()));
				fieldTypeRow.put(field.getFieldName(), new Editor<>(field.getFieldType(), field.getFieldName()));
				showTypeRow.put(field.getFieldName(), new Editor<>(field.getViews(), field.getFieldName()));
				showTypeRow.put("selection-" + field.getFieldName(), new Editor<>(field.selectionSign(service), field.getFieldName()));
				filterRow.put(field.getFieldName(), new Editor<>(field.getFilter() ? 1 : 0, field.getFieldName()));
				sortedRow.put(field.getFieldName(), new Editor<>(field.getSorted() ? 1 : 0, field.getFieldName()));
				requiredRow.put(field.getFieldName(), new Editor<>(field.getRequired() ? 1 : 0, field.getFieldName()));
				uniquedRow.put(field.getFieldName(), new Editor<>(field.getUniqued() ? 1 : 0, field.getFieldName()));
				hiddenRow.put(field.getFieldName(), new Editor<>(field.getHidden() ? 1 : 0, field.getFieldName()));
				handleDefaultMap(defaultRow, field, service);
				subremoveRow.put(field.getFieldName(), "");
			}
		} else {
			String fieldName = "field0";
			subColumns.add(Column.builder().field(fieldName).ordered(0).build());
			fieldNameRow.put(fieldName, new Editor<>("", fieldName));
			fieldTypeRow.put(fieldName, new Editor<>(grid.getClassOptions().get(0).getId(), fieldName));
			showTypeRow.put(fieldName, new Editor<>(grid.getViewOptions().get(0).getId(), fieldName));
			if (CollectionUtils.isEmpty(grid.getSelectionOptions())) {
				showTypeRow.put("selection-" + fieldName, new Editor<>(null, fieldName));
			} else {
				showTypeRow.put("selection-" + fieldName, new Editor<>(grid.getSelectionOptions().get(0).getId(), fieldName));
			}
			filterRow.put(fieldName, new Editor<>(grid.getWhetherOptions().get(0).getId(), fieldName));
			sortedRow.put(fieldName, new Editor<>(grid.getWhetherOptions().get(0).getId(), fieldName));
			requiredRow.put(fieldName, new Editor<>(grid.getWhetherOptions().get(0).getId(), fieldName));
			uniquedRow.put(fieldName, new Editor<>(grid.getWhetherOptions().get(0).getId(), fieldName));
			hiddenRow.put(fieldName, new Editor<>(grid.getWhetherOptions().get(0).getId(), fieldName));
			defaultRow.put(fieldName, new Editor<>("", fieldName));
			subremoveRow.put(fieldName, "");
		}
		Collections.sort(subColumns, (c1, c2) -> {
			if (c1.getOrdered() == null) {
				return -1;
			}
			if (c2.getOrdered() == null) {
				return 1;
			}
			return c1.getOrdered().compareTo(c2.getOrdered());
		});
		subColumns.add(Column.builder().field("add").width(50).build());
		subRows.add(fieldNameRow);
		subRows.add(fieldTypeRow);
		subRows.add(showTypeRow);
		subRows.add(filterRow);
		subRows.add(sortedRow);
		subRows.add(requiredRow);
		subRows.add(uniquedRow);
		subRows.add(hiddenRow);
		subRows.add(defaultRow);
		subRows.add(subremoveRow);
		subTable.setIdx(idx);
		subTable.setRows(subRows);
		subTable.setColumns(subColumns);
		return subTable;
	}
	
	private void handleDefaultMap(Map<String, Object> defaultRow, TemplateField field, String service) {
		if (Views.SELECTION.getKey().equals(field.getViews())) {
			String selectionSign = field.selectionSign(service);
			List<Option<Object>> options = SelectionUtils.getSelectionOptions(selectionSign, null);
			defaultRow.put(field.getFieldName(), new Editor<>(field.getDefaultValue(), field.getFieldName(), selectionSign, options));
		} else if (Views.PASSWORD.getKey().equals(field.getViews())) {
			Object value = DomainUtils.decrypt(field.getDefaultValue());
			defaultRow.put(field.getFieldName(), new Editor<>(SystemUtils.isEmpty(value) ? "" : value, field.getFieldName(), "password"));
		} else {
			String type = "text";
			if (Classes.LONG.getKey().equals(field.getFieldType()) || Classes.DOUBLE.getKey().equals(field.getFieldType())) {
				type = "number";
			} else if (Classes.DATE.getKey().equals(field.getFieldType())) {
				type = "date";
			}
			defaultRow.put(field.getFieldName(), new Editor<>(SystemUtils.isEmpty(field.getDefaultValue()) ? "" : field.getDefaultValue(), field.getFieldName(), type));
		}
	}
	
	private List<Breadcrumb> breadcrumbs(Long id) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage("system.module"))
				.to("/").build());
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage("template.management"))
				.to("/template/main").build());
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage(id == null ? "create" : "edit"))
				.active(true).build());
		return breadcrumbs;
	}
	
	private List<Integer> getFunctions(Template template, Long userId) {
		return SystemUtils.getFunctionCodes(getFunction(template, userId), ManagementMainService.SUPPORT_FUNCTIONS);
	}
	
}
