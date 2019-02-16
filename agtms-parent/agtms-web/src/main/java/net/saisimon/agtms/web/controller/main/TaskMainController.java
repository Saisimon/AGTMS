package net.saisimon.agtms.web.controller.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Task;
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
import net.saisimon.agtms.core.dto.UserInfo;
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
import net.saisimon.agtms.web.controller.base.MainController;
import net.saisimon.agtms.web.dto.resp.TaskInfo;
import net.saisimon.agtms.web.selection.HandleStatusSelection;
import net.saisimon.agtms.web.selection.TaskTypeSelection;

/**
 * 任务主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/task/main")
@Slf4j
public class TaskMainController extends MainController {
	
	public static final String TASK = "task";
	private static final String TASK_FILTERS = TASK + "_filters";
	private static final String TASK_PAGEABLE = TASK + "_pageable";
	private static final List<String> FUNCTIONS = Arrays.asList(
			Functions.REMOVE.getFunction(),
			Functions.BATCH_REMOVE.getFunction()
	);
	private static final Set<String> TASK_FILTER_FIELDS = new HashSet<>();
	static {
		TASK_FILTER_FIELDS.add("taskType");
		TASK_FILTER_FIELDS.add("taskTime");
		TASK_FILTER_FIELDS.add("handleStatus");
		TASK_FILTER_FIELDS.add("handleTime");
	}
	
	@Autowired
	private HandleStatusSelection handleStatusSelection;
	@Autowired
	private TaskTypeSelection taskTypeSelection;
	
	@PostMapping("/grid")
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(TASK));
	}
	
	@PostMapping("/list")
	public Result list(@RequestParam Map<String, Object> param, @RequestBody Map<String, Object> body) {
		UserInfo userInfo = AuthUtils.getUserInfo();
		FilterRequest filter = FilterRequest.build(body, TASK_FILTER_FIELDS);
		filter.and(Constant.OPERATORID, userInfo.getUserId());
		FilterPageable pageable = FilterPageable.build(param);
		TaskService taskService = TaskServiceFactory.get();
		Page<Task> page = taskService.findPage(filter, pageable);
		List<TaskInfo> results = new ArrayList<>(page.getContent().size());
		Map<String, String> handleStatusMap = handleStatusSelection.select();
		Map<String, String> taskTypeMap = taskTypeSelection.select();
		for (Task task : page.getContent()) {
			TaskInfo result = new TaskInfo();
			result.setId(task.getId());
			Actuator<?> actuator = ActuatorFactory.get(task.getTaskType());
			result.setTaskContent(actuator.taskContent(task));
			result.setTaskType(taskTypeMap.get(task.getTaskType().toString()));
			result.setTaskTime(task.getTaskTime());
			result.setHandleStatus(handleStatusMap.get(task.getHandleStatus().toString()));
			result.setHandleResult(getMessage(task.getHandleResult()));
			result.setHandleTime(task.getHandleTime());
			result.setAction(TASK);
			if (task.getHandleStatus() == HandleStatuses.SUCCESS.getStatus()) { // download
				result.getDisableActions().add(Boolean.FALSE);
			} else {
				result.getDisableActions().add(Boolean.TRUE);
			}
			if (task.getHandleStatus() == HandleStatuses.PROCESSING.getStatus()) { // cancel
				result.getDisableActions().add(Boolean.FALSE);
			} else {
				result.getDisableActions().add(Boolean.TRUE);
			}
			result.getDisableActions().add(Boolean.FALSE); // remove
			results.add(result);
		}
		request.getSession().setAttribute(TASK_FILTERS, body);
		request.getSession().setAttribute(TASK_PAGEABLE, param);
		return ResultUtils.pageSuccess(results, page.getTotalElements());
	}
	
	@GetMapping("/download")
	public void download(@RequestParam(name = "id") Long id) {
		try {
			TaskService taskService = TaskServiceFactory.get();
			long userId = AuthUtils.getUserInfo().getUserId();
			Task task = taskService.getTask(id, userId);
			if (task == null) {
				SystemUtils.sendObject(response, ErrorMessage.Task.TASK_NOT_EXIST);
				return;
			}
			SystemUtils.downloadTask(task, request, response);
		} catch (IOException e) {
			log.error("响应流异常", e);
		}
	}
	
	@PostMapping("/cancel")
	public Result cancel(@RequestParam(name = "id") Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		TaskService taskService = TaskServiceFactory.get();
		long userId = AuthUtils.getUserInfo().getUserId();
		Task task = taskService.getTask(id, userId);
		if (task == null) {
			return ErrorMessage.Task.TASK_NOT_EXIST;
		}
		SystemUtils.cancelTask(task);
		return ResultUtils.simpleSuccess();
	}
	
	@PostMapping("/remove")
	public Result remove(@RequestParam(name = "id") Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		TaskService taskService = TaskServiceFactory.get();
		long userId = AuthUtils.getUserInfo().getUserId();
		Task task = taskService.getTask(id, userId);
		if (task == null) {
			return ErrorMessage.Task.TASK_NOT_EXIST;
		}
		taskService.delete(id);
		return ResultUtils.simpleSuccess();
	}
	
	@PostMapping("/batch/remove")
	public Result batchRemove(@RequestBody List<Long> ids) {
		if (ids.size() == 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		long userId = AuthUtils.getUserInfo().getUserId();
		TaskService taskService = TaskServiceFactory.get();
		for (Long id : ids) {
			Task task = taskService.getTask(id, userId);
			if (task != null) {
				taskService.delete(id);
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
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.model")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("task.management")).active(true).build());
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("taskType", "taskTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("task.type", "create.time")));
		Map<String, FieldFilter> value = new HashMap<>();
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
		value = new HashMap<>();
		Map<String, String> handleStatusMap = handleStatusSelection.select();
		values = new ArrayList<>(handleStatusMap.keySet());
		texts = new ArrayList<>(handleStatusMap.values());
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getName(), values, texts));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}

	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("taskContent").label(getMessage("task.content")).view(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("taskType").label(getMessage("task.type")).view(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("taskTime").label(getMessage("create.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).view(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("handleStatus").label(getMessage("handle.status")).view(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("handleResult").label(getMessage("handle.result")).view(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("handleTime").label(getMessage("handle.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).view(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("action").label(getMessage("actions")).type("number").width(100).build());
		return columns;
	}
	
	@Override
	protected List<Action> actions(Object key) {
		UserInfo userInfo = AuthUtils.getUserInfo();
		List<Action> actions = new ArrayList<>();
		actions.add(Action.builder().key("download").icon("download").to("/task/main/download?" + AuthUtils.AUTHORIZE_UID + "=" + userInfo.getUserId() + "&" + AuthUtils.AUTHORIZE_TOKEN + "=" + userInfo.getToken() + "&id=").text(getMessage("result")).type("download").build());
		actions.add(Action.builder().key("cancel").icon("ban").to("/task/main/cancel").text(getMessage("cancel")).variant("outline-warning").type("modal").build());
		actions.add(Action.builder().key("remove").icon("trash").to("/task/main/remove").text(getMessage("remove")).variant("outline-danger").type("modal").build());
		return actions;
	}
	
	@Override
	protected List<String> functions(Object key) {
		return FUNCTIONS;
	}

}
