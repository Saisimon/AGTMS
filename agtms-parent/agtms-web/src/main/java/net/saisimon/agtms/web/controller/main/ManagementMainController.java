package net.saisimon.agtms.web.controller.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.transaction.Transactional;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Navigation;
import net.saisimon.agtms.core.domain.entity.Task;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.SelectFilter;
import net.saisimon.agtms.core.domain.filter.TextFilter;
import net.saisimon.agtms.core.domain.grid.BatchGrid.BatchEdit;
import net.saisimon.agtms.core.domain.grid.BatchGrid.BatchExport;
import net.saisimon.agtms.core.domain.grid.BatchGrid.BatchImport;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.grid.Filter;
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
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.ActuatorFactory;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.factory.TaskServiceFactory;
import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.service.GenerateService;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.service.TaskService;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.FileUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.MainController;
import net.saisimon.agtms.web.dto.req.ExportParam;
import net.saisimon.agtms.web.dto.req.ImportParam;
import net.saisimon.agtms.web.selection.FileTypeSelection;

/**
 * 自定义对象管理主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/management/main/{key}")
@ControllerInfo("management")
@Slf4j
public class ManagementMainController extends MainController {
	
	@Value("${extra.max-size.export:65535}")
	private int exportMaxSize;
	@Value("${extra.max-size.import:65535}")
	private int importMaxSize;
	
	@Autowired
	private FileTypeSelection fileTypeSelection;
	@Autowired
	private ExecutorService executorService;
	
	@PostMapping("/grid")
	public Result grid(@PathVariable("key") String key) {
		Template template = TemplateUtils.getTemplate(key, AuthUtils.getUserInfo().getUserId());
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		return ResultUtils.simpleSuccess(getMainGrid(template));
	}
	
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@PathVariable("key") String key, @RequestParam Map<String, Object> param, @RequestBody Map<String, Object> body) {
		Long userId = AuthUtils.getUserInfo().getUserId();
		Template template = TemplateUtils.getTemplate(key, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		FilterRequest filter = FilterRequest.build(body, TemplateUtils.getFilters(template));
		FilterPageable pageable = FilterPageable.build(param);
		GenerateService generateService = GenerateServiceFactory.build(template);
		Page<Domain> page = generateService.findPage(filter, pageable);
		request.getSession().setAttribute(key + "_filters", body);
		request.getSession().setAttribute(key + "_pageable", param);
		if (TemplateUtils.hasSelection(template)) {
			Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
			List<Map<String, Object>> datas = SelectionUtils.handleSelection(fieldInfoMap, template.getService(), page.getContent(), userId);
			return ResultUtils.pageSuccess(datas, page.getTotalElements());
		} else {
			return ResultUtils.pageSuccess(page.getContent(), page.getTotalElements());
		}
	}

	@Operate(type=OperateTypes.REMOVE)
	@Transactional
	@PostMapping("/remove")
	public Result remove(@PathVariable("key") String key, @RequestParam(name = "id") Long id) {
		Long userId = AuthUtils.getUserInfo().getUserId();
		Template template = TemplateUtils.getTemplate(key, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.REMOVE)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		GenerateService generateService = GenerateServiceFactory.build(template);
		Domain domain = generateService.findById(id, userId);
		if (domain != null) {
			generateService.delete(domain);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@PostMapping("/batch/grid")
	public Result batchGrid(@PathVariable("key") String key) {
		Template template = TemplateUtils.getTemplate(key, AuthUtils.getUserInfo().getUserId());
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		return ResultUtils.simpleSuccess(getBatchGrid(template));
	}
	
	@Operate(type=OperateTypes.BATCH_EDIT)
	@Transactional
	@PostMapping("/batch/save")
	public Result batchSave(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		List<Integer> ids = SystemUtils.transformList(body.get("ids"));
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Template template = TemplateUtils.getTemplate(key, AuthUtils.getUserInfo().getUserId());
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.BATCH_EDIT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		Map<String, Object> map = new HashMap<>();
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		for (Map.Entry<String, Object> entry : body.entrySet()) {
			String fieldName = entry.getKey();
			Object fieldValue = entry.getValue();
			if (StringUtils.isEmpty(fieldValue)) {
				continue;
			}
			TemplateField field = fieldInfoMap.get(fieldName);
			if (field == null || field.getUniqued()) {
				continue;
			}
			fieldValue = DomainGenerater.parseFieldValue(fieldValue, field.getFieldType());
			if (fieldValue != null) {
				map.put(fieldName, fieldValue);
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
			map.put(Constant.UPDATETIME, new Date());
			generateService.updateDomain(id.longValue(), map);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Operate(type=OperateTypes.BATCH_REMOVE)
	@Transactional
	@PostMapping("/batch/remove")
	public Result batchRemove(@PathVariable("key") String key, @RequestBody List<Long> ids) {
		Long userId = AuthUtils.getUserInfo().getUserId();
		Template template = TemplateUtils.getTemplate(key, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.BATCH_REMOVE)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		GenerateService generateService = GenerateServiceFactory.build(template);
		for (Long id : ids) {
			Domain domain = generateService.findById(id, userId);
			if (domain == null) {
				continue;
			}
			generateService.delete(domain);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Operate(type=OperateTypes.EXPORT)
	@Transactional
	@PostMapping("/batch/export")
	public Result batchExport(@PathVariable("key") String key, @Validated @RequestBody ExportParam body, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Long userId = AuthUtils.getUserInfo().getUserId();
		Template template = TemplateUtils.getTemplate(key, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.EXPORT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		GenerateService generateService = GenerateServiceFactory.build(template);
		FilterRequest filter = FilterRequest.build(body.getFilter(), TemplateUtils.getFilters(template));
		filter.and(Constant.OPERATORID, userId);
		Long total = generateService.count(filter);
		if (total > exportMaxSize) {
			Result result = ErrorMessage.Task.Export.TASK_EXPORT_MAX_SIZE_LIMIT;
			result.setMessageArgs(new Object[]{ exportMaxSize });
			return result;
		}
		body.setTemplateId(template.sign());
		body.setUserId(userId);
		if (StringUtils.isBlank(body.getExportFileName())) {
			body.setExportFileName(template.getTitle());
		}
		Task exportTask = new Task();
		exportTask.setOperatorId(userId);
		exportTask.setTaskTime(new Date());
		exportTask.setTaskType(Functions.EXPORT.getFunction());
		exportTask.setTaskParam(SystemUtils.toJson(body));
		exportTask.setHandleStatus(HandleStatuses.CREATE.getStatus());
		TaskService taskService = TaskServiceFactory.get();
		exportTask = taskService.saveOrUpdate(exportTask);
		submitTask(exportTask);
		Result result = ResultUtils.simpleSuccess();
		result.setMessage(getMessage("export.task.created"));
		return result;
	}
	
	@Operate(type=OperateTypes.IMPORT)
	@Transactional
	@PostMapping("/batch/import")
	public Result batchImport(@PathVariable("key") String key, 
			@RequestParam(name="importFileName", required=false) String importFileName, 
			@RequestParam("importFileType") String importFileType, 
			@RequestParam("importFields") List<String> importFields, 
			@RequestParam("importFile") MultipartFile importFile) {
		Long userId = AuthUtils.getUserInfo().getUserId();
		Template template = TemplateUtils.getTemplate(key, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.IMPORT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		Set<String> requireds = TemplateUtils.getRequireds(template);
		if (!CollectionUtils.isEmpty(requireds)) {
			for (String required : requireds) {
				if (!importFields.contains(required)) {
					return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
				}
			}
		}
		String path = Constant.File.IMPORT_PATH + File.separatorChar + userId;
		String name = UUID.randomUUID().toString();
		File file = new File(path + File.separator + name + "." + importFileType);
		try {
			int size = 0;
			switch (importFileType) {
				case Constant.File.XLS:
					size = FileUtils.sizeXLS(importFile.getInputStream());
					break;
				case Constant.File.CSV:
					size = FileUtils.sizeCSV(importFile.getInputStream(), ",");
					break;
				case Constant.File.XLSX:
					size = FileUtils.sizeXLSX(importFile.getInputStream());
					break;
				default:
					break;
			}
			if (size == 0) {
				return ErrorMessage.Task.Import.TASK_IMPORT_SIZE_EMPTY;
			}
			if (size > importMaxSize) {
				Result result = ErrorMessage.Task.Import.TASK_IMPORT_MAX_SIZE_LIMIT;
				result.setMessageArgs(new Object[]{ importMaxSize });
				return result;
			}
			FileUtils.createDir(file.getParentFile());
			FileOutputStream output = new FileOutputStream(file);
			IOUtils.copy(importFile.getInputStream(), output);
			output.flush();
		} catch (IOException e) {
			log.error("导入异常", e);
			return ErrorMessage.Task.Import.TASK_IMPORT_FAILED;
		}
		ImportParam body = new ImportParam();
		body.setImportFields(importFields);
		body.setImportFileName(importFileName);
		body.setImportFileType(importFileType);
		body.setImportFileUUID(name);
		body.setTemplateId(template.sign());
		body.setUserId(userId);
		Task exportTask = new Task();
		exportTask.setOperatorId(userId);
		exportTask.setTaskTime(new Date());
		exportTask.setTaskType(Functions.IMPORT.getFunction());
		exportTask.setTaskParam(SystemUtils.toJson(body));
		exportTask.setHandleStatus(HandleStatuses.CREATE.getStatus());
		TaskService taskService = TaskServiceFactory.get();
		exportTask = taskService.saveOrUpdate(exportTask);
		submitTask(exportTask);
		Result result = ResultUtils.simpleSuccess();
		result.setMessage(getMessage("import.task.created"));
		return result;
	}
	
	@Override
	protected Header header(Object key) {
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		Header header = Header.builder().title(template.getTitle()).build();
		if (template.getId() != null) {
			header.setEditUrl("/template/edit?id=" + template.getId());
		}
		if (Functions.CREATE.getCode().equals(template.getFunction() & Functions.CREATE.getCode())) {
			String sign = template.sign();
			if (sign != null) {
				header.setCreateUrl("/management/edit/" + sign);
			}
		}
		return header;
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(template.getTitle()).active(true).build());
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
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		List<Filter> filters = new ArrayList<>();
		Long userId = AuthUtils.getUserInfo().getUserId();
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
				if (Views.SELECTION.getView().equals(field.getView())) {
					String selectionSign = field.selectionSign(template.getService());
					if (selectionSign == null) {
						continue;
					}
					value.put(fieldName, SelectFilter.selectSearchableFilter("", field.getFieldType(), selectionSign, userId));
				} else if (Classes.LONG.getName().equals(field.getFieldType()) || Classes.DOUBLE.getName().equals(field.getFieldType())) {
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
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		List<Column> columns = buildColumns(template, false);
		if (TemplateUtils.hasOneOfFunctions(template, Functions.EDIT, Functions.REMOVE)) {
			columns.add(Column.builder().field("action").label(getMessage("actions")).type("number").width(100).build());
		}
		return columns;
	}

	@Override
	protected List<Action> actions(Object key) {
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		String sign = template.sign();
		List<Action> actions = new ArrayList<>();
		if (TemplateUtils.hasFunction(template, Functions.EDIT)) {
			actions.add(Action.builder().key("edit").to("/management/edit/" + sign + "?id=").icon("edit").text(getMessage("edit")).type("link").build());
		}
		if (TemplateUtils.hasFunction(template, Functions.REMOVE)) {
			actions.add(Action.builder().key("remove").to("/management/main/" + sign + "/remove").icon("trash").text(getMessage("remove")).variant("outline-danger").type("modal").build());
		}
		return actions;
	}

	@Override
	protected List<String> functions(Object key) {
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		return TemplateUtils.getFunctions(template);
	}
	
	@Override
	protected String sign(Object key) {
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		return template.sign();
	}

	@Override
	protected BatchEdit batchEdit(Object key) {
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
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
			Field<Object> field = Field.builder()
					.name(fieldName)
					.text(templateField.getFieldTitle())
					.view(templateField.getView())
					.type(templateField.getFieldType())
					.sign(templateField.selectionSign(template.getService()))
					.searchable(true)
					.build();
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
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		if (!TemplateUtils.hasFunction(template, Functions.EXPORT)) {
			return null;
		}
		BatchExport batchExport = new BatchExport();
		List<Column> columns = buildColumns(template, true);
		List<Option<String>> exportFieldOptions = new ArrayList<>(columns.size());
		for (Column column : columns) {
			exportFieldOptions.add(new Option<>(column.getField(), column.getLabel()));
		}
		batchExport.setExportFileName(template.getTitle());
		batchExport.setExportFieldOptions(exportFieldOptions);
		batchExport.setExportFileTypeOptions(Select.buildOptions(fileTypeSelection.select()));
		return batchExport;
	}

	@Override
	protected BatchImport batchImport(Object key) {
		if (!(key instanceof Template)) {
			return null;
		}
		Template template = (Template) key;
		if (!TemplateUtils.hasFunction(template, Functions.IMPORT)) {
			return null;
		}
		BatchImport batchImport = new BatchImport();
		List<Column> columns = buildColumns(template, true);
		List<Option<String>> importFieldOptions = new ArrayList<>(columns.size());
		for (Column column : columns) {
			importFieldOptions.add(new Option<>(column.getField(), column.getLabel()));
		}
		batchImport.setImportFileName(template.getTitle());
		batchImport.setImportFieldOptions(importFieldOptions);
		batchImport.setImportFileTypeOptions(Select.buildOptions(fileTypeSelection.select()));
		return batchImport;
	}
	
	private List<Column> buildColumns(Template template, boolean includeHidden) {
		List<Column> columns = new ArrayList<>();
		for (TemplateColumn templateColumn : template.getColumns()) {
			for (TemplateField templateField : templateColumn.getFields()) {
				if (!includeHidden && templateField.getHidden()) {
					continue;
				}
				Column column = Column.builder().field(templateColumn.getColumnName() + templateField.getFieldName())
						.label(templateField.getFieldTitle())
						.width(templateField.getWidth())
						.ordered(templateColumn.getOrdered() * 10 + templateField.getOrdered())
						.view(templateField.getView())
						.build();
				if (Classes.LONG.getName().equals(templateField.getFieldType())) {
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
		Collections.sort(columns, Column.COMPARATOR);
		return columns;
	}
	
	private <P> void submitTask(final Task task) {
		if (task == null) {
			return;
		}
		Future<?> future = executorService.submit(() -> {
			FilterRequest filter = FilterRequest.build().and("id", task.getId());
			TaskService taskService = TaskServiceFactory.get();
			Map<String, Object> updateMap = new HashMap<>();
			updateMap.put("handleStatus", HandleStatuses.PROCESSING.getStatus());
			taskService.batchUpdate(filter, updateMap);
			try {
				@SuppressWarnings("unchecked")
				Actuator<P> actuator = (Actuator<P>) ActuatorFactory.get(task.getTaskType());
				if (actuator == null) {
					updateMap.clear();
					updateMap.put("handleStatus", HandleStatuses.PROCESSING.getStatus());
					updateMap.put("handleTime", new Date());
					taskService.batchUpdate(filter, updateMap);
					return;
				}
				Class<P> paramClass = actuator.getParamClass();
				P param = SystemUtils.fromJson(task.getTaskParam(), paramClass);
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				Result result = actuator.execute(param);
				if (Thread.currentThread().isInterrupted()) {
					return;
				}
				updateMap.clear();
				updateMap.put("handleTime", new Date());
				if (ResultUtils.isSuccess(result)) {
					updateMap.put("handleStatus", HandleStatuses.SUCCESS.getStatus());
					updateMap.put("handleResult", result.getMessage());
					updateMap.put("taskParam", SystemUtils.toJson(param));
				} else {
					updateMap.put("handleStatus", HandleStatuses.FAILURE.getStatus());
					if (result != null) {
						updateMap.put("handleResult", result.getMessage());
					}
				}
				taskService.batchUpdate(filter, updateMap);
			} catch (InterruptedException e) {
				log.warn("任务被中断");
			} catch (Throwable e) {
				updateMap.clear();
				updateMap.put("handleStatus", HandleStatuses.FAILURE.getStatus());
				updateMap.put("handleTime", new Date());
				taskService.batchUpdate(filter, updateMap);
				log.error("任务执行异常", e);
			} finally {
				SystemUtils.removeTaskFuture(task.getId());
			}
		});
		SystemUtils.putTaskFuture(task.getId(), future);
	}
	
}
