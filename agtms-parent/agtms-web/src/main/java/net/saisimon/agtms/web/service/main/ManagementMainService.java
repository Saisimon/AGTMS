package net.saisimon.agtms.web.service.main;

import static net.saisimon.agtms.core.constant.Constant.Param.FILTER;
import static net.saisimon.agtms.core.constant.Constant.Param.PAGEABLE;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.transaction.Transactional;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Resource;
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
import net.saisimon.agtms.core.dto.SimpleResult;
import net.saisimon.agtms.core.dto.TaskParam;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.ActuatorFactory;
import net.saisimon.agtms.core.factory.FileHandlerFactory;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.TaskServiceFactory;
import net.saisimon.agtms.core.handler.FileHandler;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.core.service.TaskService;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.DomainUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.req.ExportParam;
import net.saisimon.agtms.web.dto.req.ImportParam;
import net.saisimon.agtms.web.selection.FileTypeSelection;
import net.saisimon.agtms.web.service.base.AbstractMainService;
import net.saisimon.agtms.web.service.user.UserInfoService;
import net.saisimon.agtms.web.util.FileUtils;

/**
 * 自定义对象管理主服务
 * 
 * @author saisimon
 *
 */
@Service
@Slf4j
public class ManagementMainService extends AbstractMainService {
	
	public static final List<Functions> SUPPORT_FUNCTIONS = Arrays.asList(
			Functions.VIEW,
			Functions.CREATE, 
			Functions.EDIT,
			Functions.BATCH_EDIT,
			Functions.REMOVE,
			Functions.BATCH_REMOVE,
			Functions.EXPORT,
			Functions.IMPORT
	);
	
	@Autowired
	private FileTypeSelection fileTypeSelection;
	@Autowired
	private SchedulingTaskExecutor taskThreadPool;
	@Autowired
	private AgtmsProperties agtmsProperties;
	@Autowired
	private UserInfoService userInfoService;
	
	public Result grid(String key) {
		Set<Long> userIds = userInfoService.getUserIds(AuthUtils.getUid());
		Template template = TemplateUtils.getTemplate(key, userIds);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		return ResultUtils.simpleSuccess(getMainGrid(template));
	}
	
	public Result list(String key, Map<String, Object> body) {
		Set<Long> userIds = userInfoService.getUserIds(AuthUtils.getUid());
		Template template = TemplateUtils.getTemplate(key, userIds);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		Map<String, Object> filterMap = get(body, FILTER);
		Map<String, Object> pageableMap = get(body, PAGEABLE);
		FilterRequest filter = FilterRequest.build(filterMap, TemplateUtils.getFilters(template));
		if (filter == null) {
			filter = FilterRequest.build();
		}
		filter.and(Constant.OPERATORID, userIds, Constant.Operator.IN);
		FilterPageable pageable = FilterPageable.build(pageableMap);
		List<Domain> domains = GenerateServiceFactory.build(template).findPage(filter, pageable, false).getContent();
		if (pageable.getParam() != null && Operator.GT.equals(pageable.getParam().getOperator())) {
			List<Domain> reverseDomains = new ArrayList<>(domains);
			Collections.reverse(reverseDomains);
			domains = reverseDomains;
		}
		boolean more = domains.size() < pageable.getSize();
		request.getSession().setAttribute(key + FILTER_SUFFIX, filterMap);
		request.getSession().setAttribute(key + PAGEABLE_SUFFIX, pageableMap);
		return ResultUtils.pageSuccess(DomainUtils.conversions(template, domains, userIds), more);
	}

	@Transactional(rollbackOn = Exception.class)
	public Result remove(String key, Long id) {
		Set<Long> userIds = userInfoService.getUserIds(AuthUtils.getUid());
		Template template = TemplateUtils.getTemplate(key, userIds);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.REMOVE)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		Domain domain = GenerateServiceFactory.build(template).findById(id, userIds);
		if (domain == null) {
			return ErrorMessage.Domain.DOMAIN_NOT_EXIST;
		}
		GenerateServiceFactory.build(template).delete(domain);
		return ResultUtils.simpleSuccess();
	}
	
	public Result batchGrid(String key) {
		Set<Long> userIds = userInfoService.getUserIds(AuthUtils.getUid());
		Template template = TemplateUtils.getTemplate(key, userIds);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		return ResultUtils.simpleSuccess(getBatchGrid(template));
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result batchSave(String key, Map<String, Object> body) {
		List<String> ids = SystemUtils.transformList(body.get("ids"));
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Set<Long> userIds = userInfoService.getUserIds(AuthUtils.getUid());
		Template template = TemplateUtils.getTemplate(key, userIds);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		Result buildResult = buildFieldMap(body, template);
		if (!ResultUtils.isSuccess(buildResult)) {
			return buildResult;
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> map = ((SimpleResult<Map<String, Object>>) buildResult).getData();
		if (CollectionUtils.isEmpty(map)) {
			return ResultUtils.simpleSuccess();
		}
		for (String idStr : ids) {
			Long id = Long.valueOf(idStr);
			Domain domain = GenerateServiceFactory.build(template).findById(id, userIds);
			if (domain == null) {
				continue;
			}
			map.put(Constant.UPDATETIME, new Date());
			GenerateServiceFactory.build(template).updateDomain(id, map);
		}
		return ResultUtils.simpleSuccess();
	}

	@Transactional(rollbackOn = Exception.class)
	public Result batchRemove(String key, List<Long> ids) {
		if (ids.size() == 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Set<Long> userIds = userInfoService.getUserIds(AuthUtils.getUid());
		Template template = TemplateUtils.getTemplate(key, userIds);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.BATCH_REMOVE)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		for (Long id : ids) {
			Domain domain = GenerateServiceFactory.build(template).findById(id, userIds);
			if (domain == null) {
				continue;
			}
			GenerateServiceFactory.build(template).delete(domain);
		}
		return ResultUtils.simpleSuccess();
	}
	
	public Result batchExport(String key, ExportParam body) {
		Long userId = AuthUtils.getUid();
		Set<Long> userIds = userInfoService.getUserIds(userId);
		Template template = TemplateUtils.getTemplate(key, userIds);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!TemplateUtils.hasFunction(template, Functions.EXPORT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		body.setTemplateId(template.sign());
		body.setUserId(userId);
		if (SystemUtils.isBlank(body.getExportFileName())) {
			body.setExportFileName(template.getTitle());
		}
		Task exportTask = createExportTask(body);
		try {
			submitTask(exportTask);
			Result result = new Result();
			result.setCode(ResultUtils.SUCCESS_CODE);
			result.setMessage(messageService.getMessage("export.task.created"));
			return result;
		} catch (TaskRejectedException e) {
			Result result = ErrorMessage.Task.TASK_MAX_SIZE_LIMIT;
			exportTask.setHandleTime(new Date());
			exportTask.setHandleResult(result.getMessage());
			exportTask.setHandleStatus(HandleStatuses.REJECTED.getStatus());
			TaskServiceFactory.get().saveOrUpdate(exportTask);
			return result;
		}
	}
	
	public Result batchImport(String key, String importFileName, String importFileType, List<String> importFields, MultipartFile[] importFiles) {
		if (importFiles.length > agtmsProperties.getImportFileMaxSize()) {
			Result result = ErrorMessage.Task.Import.TASK_IMPORT_FILE_MAX_SIZE_LIMIT;
			result.setMessageArgs(new Object[]{ agtmsProperties.getImportFileMaxSize() });
			return result;
		}
		Long userId = AuthUtils.getUid();
		Set<Long> userIds = userInfoService.getUserIds(userId);
		Template template = TemplateUtils.getTemplate(key, userIds);
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
		for (MultipartFile importFile : importFiles) {
			if (importFile.isEmpty()) {
				continue;
			}
			ImportParam param = new ImportParam();
			param.setImportFields(importFields);
			param.setImportFileName(importFileName + "-" + importFile.getOriginalFilename());
			param.setImportFileType(importFileType);
			param.setUuid(UUID.randomUUID().toString());
			param.setTemplateId(template.sign());
			param.setUserId(userId);
			Result result = batchImport(param, importFile);
			if (!ResultUtils.isSuccess(result)) {
				return result;
			}
		}
		Result result = new Result();
		result.setCode(ResultUtils.SUCCESS_CODE);
		result.setMessage(messageService.getMessage("import.task.created"));
		return result;
	}
	
	@Override
	protected boolean large(Object key) {
		if (!(key instanceof Template)) {
			return false;
		}
		return count((Template) key, 500L) == null;
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
		if (Functions.CREATE.getCode().equals(template.getFunctions() & Functions.CREATE.getCode())) {
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
		ResourceService resourceService = ResourceServiceFactory.get();
		if (SystemUtils.isNotBlank(template.getPath())) {
			String[] strs = template.getPath().split("/");
			for (String str : strs) {
				try {
					Resource r = resourceService.findById(Long.valueOf(str)).orElse(null);
					if (r == null) {
						continue;
					}
					breadcrumbs.add(0, Breadcrumb.builder().text(r.getName()).to("/").build());
				} catch (NumberFormatException e) {}
			}
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
		Set<Long> userIds = userInfoService.getUserIds(AuthUtils.getUid());
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
				if (Views.SELECTION.getView().equals(field.getViews())) {
					String selectionSign = field.selectionSign(template.getService());
					if (selectionSign == null) {
						continue;
					}
					value.put(fieldName, SelectFilter.selectSearchableFilter("", field.getFieldType(), selectionSign, userIds));
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
			columns.add(Column.builder().field("action").label(messageService.getMessage("actions")).type("number").width(100).build());
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
			actions.add(Action.builder().key("edit").to("/management/edit/" + sign + "?id=").icon("edit").text(messageService.getMessage("edit")).type("link").build());
		}
		if (TemplateUtils.hasFunction(template, Functions.REMOVE)) {
			actions.add(Action.builder().key("remove").to("/management/main/" + sign + "/remove").icon("trash").text(messageService.getMessage("remove")).variant("outline-danger").type("modal").build());
		}
		return actions;
	}

	@Override
	protected List<Functions> functions(Object key) {
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
					.views(templateField.getViews())
					.type(templateField.getFieldType())
					.sign(templateField.selectionSign(template.getService()))
					.searchable(true)
					.build();
			if (templateField.getRequired()) {
				field.setRequired(true);
			}
			Object value = templateField.getDefaultValue();
			if (Views.PASSWORD.getView().equals(templateField.getViews())) {
				value = DomainUtils.decrypt(value);
			}
			field.setValue(value);
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
		batchExport.setExportFileTypeOptions(Select.buildOptions(fileTypeSelection.exportSelect()));
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
		batchImport.setImportFileTypeOptions(Select.buildOptions(fileTypeSelection.importSelect()));
		return batchImport;
	}
	
	private Result buildFieldMap(Map<String, Object> body, Template template) {
		if (!TemplateUtils.hasFunction(template, Functions.BATCH_EDIT)) {
			return ErrorMessage.Template.TEMPLATE_NO_FUNCTION;
		}
		Map<String, Object> map = new HashMap<>();
		Map<String, TemplateField> fieldInfoMap = TemplateUtils.getFieldInfoMap(template);
		for (Map.Entry<String, Object> entry : body.entrySet()) {
			String fieldName = entry.getKey();
			Object fieldValue = entry.getValue();
			if (SystemUtils.isEmpty(fieldValue)) {
				continue;
			}
			TemplateField field = fieldInfoMap.get(fieldName);
			if (field == null || field.getUniqued()) {
				continue;
			}
			fieldValue = DomainUtils.parseFieldValue(fieldValue, field.getFieldType());
			Result result = TemplateUtils.validate(template, field, fieldValue);
			if (!ResultUtils.isSuccess(result)) {
				return result;
			}
			if (fieldValue == null) {
				continue;
			}
			if (Views.PASSWORD.getView().equals(field.getViews())) {
				fieldValue = DomainUtils.encrypt(fieldValue);
			}
			map.put(fieldName, fieldValue);
		}
		return ResultUtils.simpleSuccess(map);
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
						.ordered(templateColumn.getOrdered() * 10 + templateField.getOrdered())
						.views(templateField.getViews())
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
	
	private Task createExportTask(ExportParam body) {
		Task exportTask = new Task();
		exportTask.setOperatorId(body.getUserId());
		exportTask.setTaskTime(new Date());
		exportTask.setTaskType(Functions.EXPORT.getFunction());
		exportTask.setTaskParam(SystemUtils.toJson(body));
		exportTask.setHandleStatus(HandleStatuses.CREATED.getStatus());
		TaskServiceFactory.get().saveOrUpdate(exportTask);
		return exportTask;
	}
	
	private Task createImportTask(ImportParam body) {
		Task importTask = new Task();
		importTask.setOperatorId(body.getUserId());
		importTask.setTaskTime(new Date());
		importTask.setTaskType(Functions.IMPORT.getFunction());
		importTask.setTaskParam(SystemUtils.toJson(body));
		importTask.setHandleStatus(HandleStatuses.CREATED.getStatus());
		TaskServiceFactory.get().saveOrUpdate(importTask);
		return importTask;
	}
	
	private <P extends TaskParam> void submitTask(final Task task) {
		Future<?> future = taskThreadPool.submit(() -> {
			TaskService taskService = TaskServiceFactory.get();
			task.setHandleStatus(HandleStatuses.PROCESSING.getStatus());
			taskService.saveOrUpdate(task);
			try {
				@SuppressWarnings("unchecked")
				Actuator<P> actuator = (Actuator<P>) ActuatorFactory.get(task.getTaskType());
				if (actuator == null) {
					task.setHandleStatus(HandleStatuses.FAILURE.getStatus());
					task.setHandleResult("failure");
					task.setHandleTime(new Date());
					taskService.saveOrUpdate(task);
					return;
				}
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}
				P param = SystemUtils.fromJson(task.getTaskParam(), actuator.getParamClass());
				Result result = actuator.execute(param);
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException();
				}
				task.setHandleTime(new Date());
				if (ResultUtils.isSuccess(result)) {
					task.setHandleStatus(HandleStatuses.SUCCESS.getStatus());
					task.setHandleResult(result.getMessage());
					task.setTaskParam(SystemUtils.toJson(param));
				} else {
					task.setHandleStatus(HandleStatuses.FAILURE.getStatus());
					if (result != null) {
						task.setHandleResult(result.getMessage());
					} else {
						task.setHandleResult("failure");
					}
				}
				taskService.saveOrUpdate(task);
			} catch (InterruptedException e) {
				task.setHandleTime(new Date());
				task.setHandleStatus(HandleStatuses.CANCELED.getStatus());
				task.setHandleResult("task.cancel");
				taskService.saveOrUpdate(task);
				log.warn("任务被中断");
			} catch (Throwable e) {
				task.setHandleTime(new Date());
				task.setHandleStatus(HandleStatuses.FAILURE.getStatus());
				task.setHandleResult("server.error");
				taskService.saveOrUpdate(task);
				log.error("任务执行异常", e);
			} finally {
				SystemUtils.removeTaskFuture(task.getId());
			}
		});
		SystemUtils.putTaskFuture(task.getId(), future);
	}
	
	private Long count(Template template, long timeout) {
		CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
			return GenerateServiceFactory.build(template).count(null);
		}, SystemUtils.executor);
		try {
			return future.get(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | TimeoutException e) {
			return null;
		} catch (ExecutionException e) {
			log.error("Query count failed.", e);
			return -1L;
		}
	}
	
	private Result batchImport(ImportParam param, MultipartFile importFile) {
		FileHandler handler = FileHandlerFactory.getHandler(param.getImportFileType());
		if (handler == null) {
			return ErrorMessage.Common.UNSUPPORTED_FILE_TYPE;
		}
		try (FileOutputStream output = new FileOutputStream(createImportFile(param))) {
			int size = handler.size(importFile);
			if (size == 0) {
				return ErrorMessage.Task.Import.TASK_IMPORT_SIZE_EMPTY;
			}
			if (size > agtmsProperties.getImportRowsMaxSize()) {
				Result result = ErrorMessage.Task.Import.TASK_IMPORT_MAX_SIZE_LIMIT;
				result.setMessageArgs(new Object[]{ agtmsProperties.getImportRowsMaxSize() });
				return result;
			}
			IOUtils.copy(importFile.getInputStream(), output);
			output.flush();
		} catch (IOException e) {
			log.error("导入异常", e);
			return ErrorMessage.Task.Import.TASK_IMPORT_FAILED;
		}
		Task importTask = createImportTask(param);
		try {
			submitTask(importTask);
			return ResultUtils.simpleSuccess();
		} catch (TaskRejectedException e) {
			importTask.setHandleTime(new Date());
			importTask.setHandleResult(ErrorMessage.Task.TASK_MAX_SIZE_LIMIT.getMessage());
			importTask.setHandleStatus(HandleStatuses.REJECTED.getStatus());
			TaskServiceFactory.get().saveOrUpdate(importTask);
			return ErrorMessage.Task.TASK_MAX_SIZE_LIMIT;
		}
	}
	
	private File createImportFile(ImportParam param) throws IOException {
		StringBuilder importFilePath = new StringBuilder();
		importFilePath.append(agtmsProperties.getFilepath())
			.append(File.separatorChar).append(Constant.File.IMPORT_PATH)
			.append(File.separatorChar).append(param.getUserId());
		return FileUtils.createFile(importFilePath.toString(), param.getUuid(), "." + param.getImportFileType());
	}
	
}
