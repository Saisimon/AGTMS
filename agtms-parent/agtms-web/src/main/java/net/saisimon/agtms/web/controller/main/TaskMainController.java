package net.saisimon.agtms.web.controller.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Task;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.SelectFilter;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Filter;
import net.saisimon.agtms.core.domain.grid.MainGrid.Action;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.HandleStatuses;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.ActuatorFactory;
import net.saisimon.agtms.core.factory.TaskServiceFactory;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.service.TaskService;
import net.saisimon.agtms.core.task.Actuator;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.AbstractMainController;
import net.saisimon.agtms.web.dto.resp.TaskInfo;
import net.saisimon.agtms.web.selection.HandleStatusSelection;
import net.saisimon.agtms.web.selection.TaskTypeSelection;
import net.saisimon.agtms.web.selection.UserSelection;

/**
 * 任务主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/task/main")
@ControllerInfo("task.management")
@Slf4j
public class TaskMainController extends AbstractMainController {
	
	public static final String TASK = "task";
	private static final String TASK_FILTERS = TASK + "_filters";
	private static final String TASK_PAGEABLE = TASK + "_pageable";
	private static final List<String> FUNCTIONS = Arrays.asList(
			Functions.VIEW.getFunction(),
			Functions.REMOVE.getFunction(),
			Functions.BATCH_REMOVE.getFunction()
	);
	private static final Set<String> TASK_FILTER_FIELDS = new HashSet<>();
	static {
		TASK_FILTER_FIELDS.add("taskType");
		TASK_FILTER_FIELDS.add("taskTime");
		TASK_FILTER_FIELDS.add("handleStatus");
		TASK_FILTER_FIELDS.add("handleTime");
		TASK_FILTER_FIELDS.add(Constant.OPERATORID);
	}
	
	@Autowired
	private HandleStatusSelection handleStatusSelection;
	@Autowired
	private TaskTypeSelection taskTypeSelection;
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private UserSelection userSelection;
	
	@PostMapping("/grid")
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(TASK));
	}
	
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@RequestParam Map<String, Object> param, @RequestBody Map<String, Object> body) {
		FilterRequest filter = FilterRequest.build(body, TASK_FILTER_FIELDS);
		Long userId = AuthUtils.getUid();
		UserToken userToken = TokenFactory.get().getToken(userId, false);
		if (!userToken.isAdmin()) {
			filter.and(Constant.OPERATORID, userId);
		}
		FilterPageable pageable = FilterPageable.build(param);
		TaskService taskService = TaskServiceFactory.get();
		Page<Task> page = taskService.findPage(filter, pageable);
		List<TaskInfo> results = new ArrayList<>(page.getContent().size());
		Map<Integer, String> handleStatusMap = handleStatusSelection.select();
		Map<String, String> taskTypeMap = taskTypeSelection.select();
		Map<Long, String> userMap = userSelection.select();
		for (Task task : page.getContent()) {
			TaskInfo result = new TaskInfo();
			result.setId(task.getId());
			Actuator<?> actuator = ActuatorFactory.get(task.getTaskType());
			result.setTaskContent(actuator.taskContent(task));
			result.setTaskType(taskTypeMap.get(task.getTaskType().toString()));
			result.setTaskTime(task.getTaskTime());
			result.setHandleStatus(handleStatusMap.get(task.getHandleStatus()));
			if (SystemUtils.isNotBlank(task.getHandleResult())) {
				result.setHandleResult(getMessage(task.getHandleResult()));
			}
			result.setHandleTime(task.getHandleTime());
			result.setOperator(userMap.get(task.getOperatorId()));
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
		request.getSession().setAttribute(TASK_FILTERS, body);
		request.getSession().setAttribute(TASK_PAGEABLE, param);
		return ResultUtils.pageSuccess(results, page.getTotalElements());
	}
	
	@Operate(type=OperateTypes.QUERY, value="download")
	@Transactional(rollbackOn = Exception.class)
	@GetMapping("/download")
	public void download(@RequestParam(name = "id") Long id) {
		try {
			TaskService taskService = TaskServiceFactory.get();
			Long userId = AuthUtils.getUid();
			Task task = taskService.getTask(id, userId);
			if (task == null) {
				SystemUtils.sendObject(response, ErrorMessage.Task.TASK_NOT_EXIST);
				return;
			}
			if (task == null || !HandleStatuses.SUCCESS.getStatus().equals(task.getHandleStatus())) {
				response.sendError(HttpStatus.NOT_FOUND.value());
				return;
			}
			Actuator<?> actuator = ActuatorFactory.get(task.getTaskType());
			if (actuator == null) {
				response.sendError(HttpStatus.NOT_FOUND.value());
				return;
			}
			actuator.download(task, request, response);
		} catch (IOException e) {
			log.error("响应流异常", e);
		}
	}
	
	@Operate(type=OperateTypes.QUERY, value="cancel")
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/cancel")
	public Result cancel(@RequestParam(name = "id") Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		TaskService taskService = TaskServiceFactory.get();
		Task task = taskService.getTask(id, AuthUtils.getUid());
		if (task == null) {
			return ErrorMessage.Task.TASK_NOT_EXIST;
		}
		task.setHandleStatus(HandleStatuses.CANCELING.getStatus());
		taskService.saveOrUpdate(task);
		cancelTask(task);
		return ResultUtils.simpleSuccess();
	}
	
	@Operate(type=OperateTypes.REMOVE)
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/remove")
	public Result remove(@RequestParam(name = "id") Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		TaskService taskService = TaskServiceFactory.get();
		Task task = taskService.getTask(id, AuthUtils.getUid());
		if (task == null) {
			return ErrorMessage.Task.TASK_NOT_EXIST;
		}
		cancelTask(task);
		taskService.delete(task);
		return ResultUtils.simpleSuccess();
	}
	
	@Operate(type=OperateTypes.BATCH_REMOVE)
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/batch/remove")
	public Result batchRemove(@RequestBody List<Long> ids) {
		if (ids.size() == 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Long userId = AuthUtils.getUid();
		TaskService taskService = TaskServiceFactory.get();
		for (Long id : ids) {
			Task task = taskService.getTask(id, userId);
			if (task != null) {
				taskService.delete(task);
			}
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected Header header(Object key) {
		return Header.builder().title(getMessage("task.management")).build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("task.management")).active(true).build());
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
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getName(), values, texts));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("handleStatus", "handleTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("handle.status", "handle.time")));
		value = new HashMap<>(4);
		Map<Integer, String> handleStatusMap = handleStatusSelection.select();
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getName(), new ArrayList<>(handleStatusMap.keySet()), new ArrayList<>(handleStatusMap.values())));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("operatorId");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("operator")));
		value = new HashMap<>(4);
		Map<Long, String> userMap = userSelection.select();
		List<Long> userValues = new ArrayList<>(userMap.size());
		List<String> userTexts = new ArrayList<>(userMap.size());
		for (Entry<Long, String> entry : userMap.entrySet()) {
			userValues.add(entry.getKey());
			userTexts.add(entry.getValue());
		}
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getName(), userValues, userTexts));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}

	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("taskContent").label(getMessage("task.content")).views(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("taskType").label(getMessage("task.type")).views(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("taskTime").label(getMessage("create.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("handleStatus").label(getMessage("handle.status")).views(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("handleResult").label(getMessage("handle.result")).views(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("handleTime").label(getMessage("handle.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("operator").label(getMessage("operator")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("action").label(getMessage("actions")).type("number").width(100).build());
		return columns;
	}
	
	@Override
	protected List<Action> actions(Object key) {
		List<Action> actions = new ArrayList<>();
		actions.add(Action.builder().key("download").icon("download").to("/task/main/download?" + AuthUtils.AUTHORIZE_UID + "=" + AuthUtils.getUid() + "&" + AuthUtils.AUTHORIZE_TOKEN + "=" + AuthUtils.getToken() + "&id=").text(getMessage("result")).type("download").build());
		actions.add(Action.builder().key("cancel").icon("ban").to("/task/main/cancel").text(getMessage("cancel")).variant("outline-warning").type("modal").build());
		actions.add(Action.builder().key("remove").icon("trash").to("/task/main/remove").text(getMessage("remove")).variant("outline-danger").type("modal").build());
		return actions;
	}
	
	@Override
	protected List<String> functions(Object key) {
		return FUNCTIONS;
	}
	
	private void cancelTask(Task task) {
		if (task == null || SystemUtils.isBlank(task.getIp()) || task.getPort() == null) {
			return;
		}
		String url = "http://" + task.getIp() + ":" + task.getPort() + "/api/cancel/task?taskId=" + task.getId();
		restTemplate.getForObject(url, Void.class);
	}

}
