package net.saisimon.agtms.web.service.main;

import static net.saisimon.agtms.core.constant.Constant.Param.FILTER;
import static net.saisimon.agtms.core.constant.Constant.Param.PAGEABLE;
import static net.saisimon.agtms.core.constant.Constant.Param.PARAM;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Task;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.SelectFilter;
import net.saisimon.agtms.core.domain.grid.BatchOperate;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Filter;
import net.saisimon.agtms.core.domain.grid.MainGrid.Action;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.BaseTaskParam;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.ActuatorFactory;
import net.saisimon.agtms.core.factory.TaskServiceFactory;
import net.saisimon.agtms.core.service.TaskService;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.resp.TaskInfo;
import net.saisimon.agtms.web.selection.HandleStatusSelection;
import net.saisimon.agtms.web.selection.TaskTypeSelection;
import net.saisimon.agtms.web.selection.UserSelection;
import net.saisimon.agtms.web.service.base.AbstractMainService;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 任务主服务
 * 
 * @author saisimon
 *
 */
@Service
@Slf4j
public class TaskMainService extends AbstractMainService {
	
	public static final String TASK = "task";
	public static final List<Functions> SUPPORT_FUNCTIONS = Arrays.asList(
			Functions.VIEW,
			Functions.EDIT,
			Functions.REMOVE,
			Functions.BATCH_REMOVE
	);
	
	private static final String TASK_FILTERS = TASK + FILTER_SUFFIX;
	private static final String TASK_PAGEABLE = TASK + PAGEABLE_SUFFIX;
	private static final Set<String> TASK_FILTER_FIELDS = new HashSet<>();
	static {
		TASK_FILTER_FIELDS.add("taskType");
		TASK_FILTER_FIELDS.add("taskTime");
		TASK_FILTER_FIELDS.add("handleStatus");
		TASK_FILTER_FIELDS.add("handleTime");
		TASK_FILTER_FIELDS.add(Constant.OPERATORID);
	}
	
	@Value("${spring.application.name}")
	private String applicationName;
	
	@Autowired
	private HandleStatusSelection handleStatusSelection;
	@Autowired
	private TaskTypeSelection taskTypeSelection;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private UserSelection userSelection;
	@Autowired(required = false)
	private DiscoveryClient discoveryClient;
	@Autowired
	private PremissionService premissionService;
	
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(TASK));
	}
	
	public <P extends BaseTaskParam> Result list(Map<String, Object> body) {
		Map<String, Object> filterMap = get(body, FILTER);
		Map<String, Object> pageableMap = get(body, PAGEABLE);
		FilterRequest filter = FilterRequest.build(filterMap, TASK_FILTER_FIELDS);
		if (filter == null) {
			filter = FilterRequest.build();
		}
		filter.and(Constant.OPERATORID, premissionService.getUserIds(AuthUtils.getUid()), Constant.Operator.IN);
		if (pageableMap != null) {
			pageableMap.remove(PARAM);
		}
		FilterPageable pageable = FilterPageable.build(pageableMap);
		TaskService taskService = TaskServiceFactory.get();
		List<Task> list = taskService.findPage(filter, pageable, false).getContent();
		List<TaskInfo> results = new ArrayList<>(list.size());
		Map<Integer, String> handleStatusMap = handleStatusSelection.select();
		Map<String, String> taskTypeMap = taskTypeSelection.select();
		Map<String, String> userMap = userSelection.select();
		for (Task task : list) {
			TaskInfo result = new TaskInfo();
			result.setId(task.getId().toString());
			@SuppressWarnings("unchecked")
			Actuator<P> actuator = (Actuator<P>) ActuatorFactory.get(task.getTaskType());
			P param = SystemUtils.fromJson(task.getTaskParam(), actuator.getParamClass());
			result.setTaskContent(actuator.taskContent(param));
			result.setTaskType(taskTypeMap.get(task.getTaskType().toString()));
			result.setTaskTime(task.getTaskTime());
			result.setHandleStatus(handleStatusMap.get(task.getHandleStatus()));
			result.setHandleResult(actuator.handleResult(task.getHandleResult()));
			result.setHandleTime(task.getHandleTime());
			result.setOperator(userMap.get(task.getOperatorId().toString()));
			result.setUuid(param.getUuid());
			result.setAction(TASK);
			// download
			if (HandleStatuses.SUCCESS.getStatus().equals(task.getHandleStatus())) {
				result.getDisableActions().add(Boolean.FALSE);
			} else {
				result.getDisableActions().add(Boolean.TRUE);
			}
			// cancel
			if (HandleStatuses.PROCESSING.getStatus().equals(task.getHandleStatus())) {
				result.getDisableActions().add(Boolean.FALSE);
			} else {
				result.getDisableActions().add(Boolean.TRUE);
			}
			// remove
			result.getDisableActions().add(Boolean.FALSE);
			results.add(result);
		}
		HttpSession session = request.getSession();
		session.setAttribute(TASK_FILTERS, filterMap);
		session.setAttribute(TASK_PAGEABLE, pageableMap);
		return ResultUtils.pageSuccess(results, list.size() < pageable.getSize());
	}
	
	@Transactional(rollbackOn = Exception.class)
	public <P extends BaseTaskParam> void download(Long id, String uuid) throws IOException {
		TaskService taskService = TaskServiceFactory.get();
		Task task = taskService.findById(id).orElse(null);
		if (task == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		if (!HandleStatuses.SUCCESS.getStatus().equals(task.getHandleStatus())) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		@SuppressWarnings("unchecked")
		Actuator<P> actuator = (Actuator<P>) ActuatorFactory.get(task.getTaskType());
		if (actuator == null) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		P param = SystemUtils.fromJson(task.getTaskParam(), actuator.getParamClass());
		if (!uuid.equals(param.getUuid())) {
			response.sendError(HttpStatus.NOT_FOUND.value());
			return;
		}
		actuator.download(param, request, response);
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result cancel(Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		TaskService taskService = TaskServiceFactory.get();
		Task task = taskService.findById(id).orElse(null);
		if (task == null) {
			return ErrorMessage.Task.TASK_NOT_EXIST;
		}
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		if (!userIds.contains(task.getOperatorId())) {
			return ErrorMessage.Task.TASK_NOT_EXIST;
		}
		if (HandleStatuses.PROCESSING.getStatus() < task.getHandleStatus()) {
			return ErrorMessage.Task.TASK_CANCEL_FAILED;
		}
		if (!cancelTask(task)) {
			return ErrorMessage.Task.TASK_CANCEL_FAILED;
		}
		task.setHandleStatus(HandleStatuses.CANCELING.getStatus());
		taskService.saveOrUpdate(task);
		return ResultUtils.simpleSuccess();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result remove(Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		TaskService taskService = TaskServiceFactory.get();
		Task task = taskService.findById(id).orElse(null);
		if (task == null) {
			return ErrorMessage.Task.TASK_NOT_EXIST;
		}
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		if (!userIds.contains(task.getOperatorId())) {
			return ErrorMessage.Task.TASK_NOT_EXIST;
		}
		if (HandleStatuses.PROCESSING.getStatus().equals(task.getHandleStatus())) {
			cancelTask(task);
		}
		deleteTaskFile(task);
		taskService.delete(task);
		return ResultUtils.simpleSuccess();
	}
	
	public Result batchGrid(String type, String func) {
		return ResultUtils.simpleSuccess(getBatchGrid(TASK, type, func));
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result batchRemove(Map<String, Object> body) {
		List<Object> ids = SystemUtils.transformList(body.get("ids"));
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		TaskService taskService = TaskServiceFactory.get();
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		for (Object idObj : ids) {
			if (idObj == null) {
				continue;
			}
			Long id = Long.valueOf(idObj.toString());
			Task task = taskService.findById(id).orElse(null);
			if (task == null || !userIds.contains(task.getOperatorId())) {
				continue;
			}
			if (HandleStatuses.PROCESSING.getStatus().equals(task.getHandleStatus())) {
				cancelTask(task);
			}
			deleteTaskFile(task);
			taskService.delete(task);
		}
		return ResultUtils.simpleSuccess();
	}
	
	public void cancelTask(Long taskId) {
		Future<?> future = SystemUtils.removeTaskFuture(taskId);
		if (future != null) {
			future.cancel(true);
		}
	}
	
	@Override
	protected Header header(Object key, List<Functions> functions) {
		return Header.builder().title(messageService.getMessage("task.management")).build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage("system.module"))
				.to("/").build());
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage("task.management"))
				.active(true).build());
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("taskType", "taskTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("task.type", "create.time")));
		Map<String, FieldFilter> value = new HashMap<>(4);
		Map<String, String> taskTypeMap = taskTypeSelection.select();
		List<String> values = new ArrayList<>(taskTypeMap.keySet());
		List<String> texts = new ArrayList<>(taskTypeMap.values());
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getKey(), values, texts));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getKey(), "", Classes.DATE.getKey()));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("handleStatus", "handleTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("handle.status", "handle.time")));
		value = new HashMap<>(4);
		Map<Integer, String> handleStatusMap = handleStatusSelection.select();
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getKey(), new ArrayList<>(handleStatusMap.keySet()), new ArrayList<>(handleStatusMap.values())));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getKey(), "", Classes.DATE.getKey()));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList(Constant.OPERATORID);
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("operator")));
		value = new HashMap<>(4);
		Map<String, String> userMap = userSelection.select();
		List<String> userValues = new ArrayList<>(userMap.size());
		List<String> userTexts = new ArrayList<>(userMap.size());
		for (Entry<String, String> entry : userMap.entrySet()) {
			userValues.add(entry.getKey());
			userTexts.add(entry.getValue());
		}
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getKey(), userValues, userTexts));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}

	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder()
				.field("taskContent")
				.label(messageService.getMessage("task.content"))
				.views(Views.TEXT.getKey())
				.width(200).build());
		columns.add(Column.builder()
				.field("taskType")
				.label(messageService.getMessage("task.type"))
				.views(Views.TEXT.getKey())
				.width(200).build());
		columns.add(Column.builder()
				.field("taskTime")
				.label(messageService.getMessage("create.time"))
				.type("date")
				.dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ")
				.dateOutputFormat("YYYY-MM-DD HH:mm:ss")
				.width(400)
				.views(Views.TEXT.getKey())
				.sortable(true)
				.orderBy("").build());
		columns.add(Column.builder()
				.field("handleStatus")
				.label(messageService.getMessage("handle.status"))
				.views(Views.TEXT.getKey())
				.width(200).build());
		columns.add(Column.builder()
				.field("handleResult")
				.label(messageService.getMessage("handle.result"))
				.views(Views.TEXT.getKey())
				.width(200).build());
		columns.add(Column.builder()
				.field("handleTime")
				.label(messageService.getMessage("handle.time"))
				.type("date")
				.dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ")
				.dateOutputFormat("YYYY-MM-DD HH:mm:ss")
				.width(400)
				.views(Views.TEXT.getKey())
				.sortable(true)
				.orderBy("").build());
		columns.add(Column.builder()
				.field("operator")
				.label(messageService.getMessage("operator"))
				.width(200)
				.views(Views.TEXT.getKey()).build());
		columns.add(Column.builder()
				.field("action")
				.label(messageService.getMessage("actions"))
				.type("number")
				.width(100).build());
		return columns;
	}
	
	@Override
	protected List<Action> actions(Object key, List<Functions> functions) {
		List<Action> actions = new ArrayList<>();
		actions.add(Action.builder().key("download").icon("download").to("/task/main/download").text(messageService.getMessage("result")).type("download").build());
		if (SystemUtils.hasFunction(Functions.EDIT.getCode(), functions)) {
			actions.add(Action.builder()
					.key("cancel")
					.icon("ban")
					.to("/task/main/cancel")
					.text(messageService.getMessage("cancel"))
					.variant("outline-warning")
					.type("modal").build());
		}
		if (SystemUtils.hasFunction(Functions.REMOVE.getCode(), functions)) {
			actions.add(Action.builder()
					.key("remove")
					.icon("trash")
					.to("/task/main/remove")
					.text(messageService.getMessage("remove"))
					.variant("outline-danger")
					.type("modal").build());
		}
		return actions;
	}
	
	@Override
	protected List<Functions> functions(Object key) {
		return functions("/task/main", null, SUPPORT_FUNCTIONS);
	}
	
	@Override
	protected BatchOperate batchOperate(Object key, String func, List<Functions> functions) {
		BatchOperate batchOperate = new BatchOperate();
		switch (func) {
		case "batchRemove":
			if (SystemUtils.hasFunction(Functions.BATCH_REMOVE.getCode(), functions)) {
				batchOperate.setPath("/batch/remove");
				return batchOperate;
			} else {
				return null;
			}
		default:
			return null;
		}
	}
	
	private <P extends BaseTaskParam> void deleteTaskFile(Task task) {
		if (task == null) {
			return;
		}
		@SuppressWarnings("unchecked")
		Actuator<P> actuator = (Actuator<P>) ActuatorFactory.get(task.getTaskType());
		if (actuator == null) {
			return;
		}
		P param = SystemUtils.fromJson(task.getTaskParam(), actuator.getParamClass());
		try {
			actuator.delete(param);
		} catch (Exception e) {
			log.error("删除任务相关资源失败", e);
		}
	}
	
	private boolean cancelTask(Task task) {
		if (discoveryClient != null) {
			List<ServiceInstance> instances = discoveryClient.getInstances(applicationName);
			if (instances == null) {
				return true;
			}
			MultiValueMap<String, Object> map= new LinkedMultiValueMap<>();
			map.add("taskId", task.getId());
			HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, new HttpHeaders());
			for (ServiceInstance instance : instances) {
				try {
					restTemplate.postForEntity(instance.getUri() + "/api/cancel/task", request, Void.class);
				} catch (RestClientException e) {
					log.error("取消任务失败", e);
					return false;
				}
			}
		} else {
			Future<?> future = SystemUtils.removeTaskFuture(task.getId());
			if (future != null) {
				return future.cancel(true);
			}
		}
		return true;
	}

}
