package net.saisimon.agtms.web.controller.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.entity.Navigation;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Editor;
import net.saisimon.agtms.core.domain.grid.TemplateGrid;
import net.saisimon.agtms.core.domain.grid.TemplateGrid.Column;
import net.saisimon.agtms.core.domain.grid.TemplateGrid.Table;
import net.saisimon.agtms.core.domain.sign.Sign;
import net.saisimon.agtms.core.domain.tag.MultipleSelect;
import net.saisimon.agtms.core.domain.tag.Select;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.EditorTypes;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.SelectionService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.NavigationUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.BaseController;
import net.saisimon.agtms.web.selection.ClassSelection;
import net.saisimon.agtms.web.selection.DataSourceSelection;
import net.saisimon.agtms.web.selection.FunctionSelection;
import net.saisimon.agtms.web.selection.NavigationSelection;
import net.saisimon.agtms.web.selection.ViewSelection;
import net.saisimon.agtms.web.selection.WhetherSelection;

/**
 * 模板编辑控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/template/edit")
@ControllerInfo("template.management")
public class TemplateEditController extends BaseController {
	
	@Autowired
	private ClassSelection classSelection;
	@Autowired
	private WhetherSelection whetherSelection;
	@Autowired
	private ViewSelection viewSelection;
	@Autowired
	private FunctionSelection functionSelection;
	@Autowired
	private NavigationSelection navigationSelection;
	@Autowired
	private DataSourceSelection dataSourceSelection;
	
	@PostMapping("/grid")
	public Result grid(@RequestParam(name = "id", required = false) Long id) {
		Long userId = AuthUtils.getUid();
		TemplateGrid grid = new TemplateGrid();
		grid.setBreadcrumbs(breadcrumbs(id));
		grid.setClassOptions(Select.buildOptions(classSelection.select()));
		grid.setViewOptions(Select.buildOptions(viewSelection.select()));
		grid.setWhetherOptions(Select.buildOptions(whetherSelection.select()));
		SelectionService selectionService = SelectionServiceFactory.get();
		grid.setSelectionOptions(Select.buildOptions(selectionService.getSelectionMap(userId)));
		Template template = TemplateUtils.getTemplate(id, userId);
		grid.setTable(buildTable(grid, template));
		MultipleSelect<Integer> functionSelect = new MultipleSelect<>();
		functionSelect.setOptions(Select.buildOptions(functionSelection.select()));
		SingleSelect<String> navigationSelect = new SingleSelect<>();
		navigationSelect.setOptions(Select.buildOptions(navigationSelection.select()));
		SingleSelect<String> dataSourceSelect = new SingleSelect<>();
		dataSourceSelect.setOptions(Select.buildOptions(dataSourceSelection.select()));
		if (template != null) {
			grid.setTitle(new Editor<>(template.getTitle(), "title"));
			List<Integer> functionCodes = TemplateUtils.getFunctionCodes(template);
			if (CollectionUtils.isEmpty(functionCodes)) {
				functionSelect.setSelected(new ArrayList<>());
			} else {
				functionSelect.setSelected(Select.getOption(functionSelect.getOptions(), functionCodes));
			}
			navigationSelect.setSelected(Select.getOption(navigationSelect.getOptions(), template.getNavigationId().toString()));
			dataSourceSelect.setSelected(Select.getOption(dataSourceSelect.getOptions(), template.getSource()));
		} else {
			grid.setTitle(new Editor<>("", "title"));
			functionSelect.setSelected(new ArrayList<>());
			navigationSelect.setSelected(navigationSelect.getOptions().get(0));
			dataSourceSelect.setSelected(dataSourceSelect.getOptions().get(0));
		}
		grid.setFunctionSelect(functionSelect);
		grid.setNavigationSelect(navigationSelect);
		grid.setDataSourceSelect(dataSourceSelect);
		return ResultUtils.simpleSuccess(grid);
	}

	@Operate(type=OperateTypes.EDIT)
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/save")
	public Result save(@RequestBody Template template) throws GenerateException {
		List<Sign> signs = GenerateServiceFactory.getSigns();
		if (CollectionUtils.isEmpty(signs)) {
			return ErrorMessage.Common.SERVER_ERROR;
		}
		if (!TemplateUtils.checkRequired(template)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		if (template.getTitle().length() > 32) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(getMessage("title"), 32);
		}
		Long userId = AuthUtils.getUid();
		if (template.getNavigationId() != null && template.getNavigationId() > 0) {
			Navigation navigation = NavigationUtils.getNavigation(template.getNavigationId(), userId);
			if (navigation == null) {
				return ErrorMessage.Navigation.NAVIGATION_NOT_EXIST;
			}
		} else {
			template.setNavigationId(-1L);
		}
		TemplateService templateService = TemplateServiceFactory.get();
		if (template.getId() != null) {
			Template oldTemplate = TemplateUtils.getTemplate(template.getId(), userId);
			if (oldTemplate == null) {
				return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
			}
			if (!template.getTitle().equals(oldTemplate.getTitle())) {
				if (templateService.exists(template.getTitle(), userId)) {
					return ErrorMessage.Template.TEMPLATE_ALREADY_EXISTS;
				}
			}
			if (!templateService.alterTable(template, oldTemplate)) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return ErrorMessage.Template.TEMPLATE_MODIFY_FAILED;
			}
			updateOldTemplate(template, oldTemplate);
			templateService.saveOrUpdate(oldTemplate);
		} else {
			if (templateService.exists(template.getTitle(), userId)) {
				return ErrorMessage.Template.TEMPLATE_ALREADY_EXISTS;
			}
			checkSource(template, signs);
			updateNewTemplate(template, userId);
			templateService.saveOrUpdate(template);
			if (!templateService.createTable(template)) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return ErrorMessage.Template.TEMPLATE_CREATE_FAILED;
			}
		}
		return ResultUtils.simpleSuccess();
	}

	private void updateNewTemplate(Template template, Long userId) {
		Date time = new Date();
		template.setCreateTime(time);
		template.setOperatorId(userId);
		template.setUpdateTime(time);
		for (TemplateColumn column : template.getColumns()) {
			for (TemplateField field : column.getFields()) {
				column.addField(field);
			}
			template.addColumn(column);
		}
	}

	private void updateOldTemplate(Template template, Template oldTemplate) {
		oldTemplate.setNavigationId(template.getNavigationId());
		oldTemplate.setFunctions(template.getFunctions());
		oldTemplate.setColumnIndex(template.getColumnIndex());
		oldTemplate.setTitle(template.getTitle());
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
					oldTemplateColumn.addField(field);
				}
				for (TemplateField oldField : oldFieldMap.values()) {
					oldTemplateColumn.removeField(oldField);
				}
				column.setId(oldTemplateColumn.getId());
				oldTemplate.removeColumn(oldTemplateColumn);
			} else {
				for (TemplateField field : column.getFields()) {
					column.addField(field);
				}
			}
			oldTemplate.addColumn(column);
		}
		for (TemplateColumn oldColumn : oldColumnMap.values()) {
			oldTemplate.removeColumn(oldColumn);
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
		Map<String, Object> columnNameRow = TemplateGrid.buildRow("columnName", getMessage("column.name"), EditorTypes.INPUT);
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
		Map<String, Object> defaultRow = TemplateGrid.buildRow("default", null, EditorTypes.INPUT);
		Map<String, Object> subremoveRow = TemplateGrid.buildRow("remove", null, EditorTypes.REMOVE);
		Integer idx = 1;
		if (column != null) {
			idx = column.getFieldIndex();
			for (TemplateField field : column.getFields()) {
				subColumns.add(Column.builder().field(field.getFieldName()).ordered(field.getOrdered()).build());
				fieldNameRow.put(field.getFieldName(), new Editor<>(field.getFieldTitle(), field.getFieldName()));
				fieldTypeRow.put(field.getFieldName(), new Editor<>(Select.getOption(grid.getClassOptions(), field.getFieldType()), field.getFieldName()));
				showTypeRow.put(field.getFieldName(), new Editor<>(Select.getOption(grid.getViewOptions(), field.getViews()), field.getFieldName()));
				showTypeRow.put("selection-" + field.getFieldName(), new Editor<>(Select.getOption(grid.getSelectionOptions(), field.selectionSign(service)), field.getFieldName()));
				filterRow.put(field.getFieldName(), new Editor<>(Select.getOption(grid.getWhetherOptions(), field.getFilter() ? 1 : 0), field.getFieldName()));
				sortedRow.put(field.getFieldName(), new Editor<>(Select.getOption(grid.getWhetherOptions(), field.getSorted() ? 1 : 0), field.getFieldName()));
				requiredRow.put(field.getFieldName(), new Editor<>(Select.getOption(grid.getWhetherOptions(), field.getRequired() ? 1 : 0), field.getFieldName()));
				uniquedRow.put(field.getFieldName(), new Editor<>(Select.getOption(grid.getWhetherOptions(), field.getUniqued() ? 1 : 0), field.getFieldName()));
				hiddenRow.put(field.getFieldName(), new Editor<>(Select.getOption(grid.getWhetherOptions(), field.getHidden() ? 1 : 0), field.getFieldName()));
				defaultRow.put(field.getFieldName(), new Editor<>(SystemUtils.isEmpty(field.getDefaultValue()) ? "" : field.getDefaultValue(), field.getFieldName()));
				subremoveRow.put(field.getFieldName(), "");
			}
		} else {
			String fieldName = "field0";
			subColumns.add(Column.builder().field(fieldName).ordered(0).build());
			fieldNameRow.put(fieldName, new Editor<>("", fieldName));
			fieldTypeRow.put(fieldName, new Editor<>(grid.getClassOptions().get(0), fieldName));
			showTypeRow.put(fieldName, new Editor<>(grid.getViewOptions().get(0), fieldName));
			if (CollectionUtils.isEmpty(grid.getSelectionOptions())) {
				showTypeRow.put("selection-" + fieldName, new Editor<>(null, fieldName));
			} else {
				showTypeRow.put("selection-" + fieldName, new Editor<>(grid.getSelectionOptions().get(0), fieldName));
			}
			filterRow.put(fieldName, new Editor<>(grid.getWhetherOptions().get(0), fieldName));
			sortedRow.put(fieldName, new Editor<>(grid.getWhetherOptions().get(0), fieldName));
			requiredRow.put(fieldName, new Editor<>(grid.getWhetherOptions().get(0), fieldName));
			uniquedRow.put(fieldName, new Editor<>(grid.getWhetherOptions().get(0), fieldName));
			hiddenRow.put(fieldName, new Editor<>(grid.getWhetherOptions().get(0), fieldName));
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
	
	private List<Breadcrumb> breadcrumbs(Long id) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("template.management")).to("/template/main").build());
		if (id == null) {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("create")).active(true).build());
		} else {
			breadcrumbs.add(Breadcrumb.builder().text(getMessage("edit")).active(true).build());
		}
		return breadcrumbs;
	}
	
}
