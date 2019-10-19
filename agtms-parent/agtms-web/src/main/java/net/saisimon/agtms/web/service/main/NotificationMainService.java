package net.saisimon.agtms.web.service.main;

import static net.saisimon.agtms.core.constant.Constant.Param.FILTER;
import static net.saisimon.agtms.core.constant.Constant.Param.PAGEABLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Notification;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.SelectFilter;
import net.saisimon.agtms.core.domain.grid.BatchOperate;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Filter;
import net.saisimon.agtms.core.domain.grid.MainGrid.Action;
import net.saisimon.agtms.core.domain.grid.MainGrid.Batch;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.NotificationStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.NotificationServiceFactory;
import net.saisimon.agtms.core.service.NotificationService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.resp.NotificationInfo;
import net.saisimon.agtms.web.selection.NotificationStatusSelection;
import net.saisimon.agtms.web.selection.NotificationTypeSelection;
import net.saisimon.agtms.web.service.base.AbstractMainService;

/**
 * 消息通知主服务
 * 
 * @author saisimon
 *
 */
@Service
public class NotificationMainService extends AbstractMainService {
	
	public static final String NOTIFICATION = "notification";
	public static final List<Functions> SUPPORT_FUNCTIONS = Arrays.asList(
			Functions.VIEW,
			Functions.REMOVE,
			Functions.BATCH_REMOVE
	);
	
	private static final String NOTIFICATION_FILTERS = NOTIFICATION + FILTER_SUFFIX;
	private static final String NOTIFICATION_PAGEABLE = NOTIFICATION + PAGEABLE_SUFFIX;
	private static final Set<String> NOTIFICATION_FILTER_FIELDS = new HashSet<>();
	static {
		NOTIFICATION_FILTER_FIELDS.add("type");
		NOTIFICATION_FILTER_FIELDS.add("status");
		NOTIFICATION_FILTER_FIELDS.add(Constant.CREATETIME);
	}
	
	@Autowired
	private NotificationTypeSelection notificationTypeSelection;
	@Autowired
	private NotificationStatusSelection notificationStatusSelection;
	
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(NOTIFICATION));
	}
	
	public Result list(Map<String, Object> body) {
		Map<String, Object> filterMap = get(body, FILTER);
		Map<String, Object> pageableMap = get(body, PAGEABLE);
		FilterRequest filter = FilterRequest.build(filterMap, NOTIFICATION_FILTER_FIELDS);
		if (filter == null) {
			filter = FilterRequest.build();
		}
		filter.and(Constant.OPERATORID, AuthUtils.getUid());
		FilterPageable pageable = FilterPageable.build(pageableMap);
		NotificationService notificationService = NotificationServiceFactory.get();
		List<Notification> list = notificationService.findPage(filter, pageable, false).getContent();
		request.getSession().setAttribute(NOTIFICATION_FILTERS, filterMap);
		request.getSession().setAttribute(NOTIFICATION_PAGEABLE, pageableMap);
		return ResultUtils.pageSuccess(buildNotificationInfos(list), list.size() < pageable.getSize());
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result remove(Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		NotificationService notificationService = NotificationServiceFactory.get();
		Notification notification = notificationService.findById(id.longValue()).orElse(null);
		if (notification == null || !notification.getOperatorId().equals(AuthUtils.getUid())) {
			return ErrorMessage.Notification.NOTIFICATION_NOT_EXIST;
		}
		notificationService.delete(notification);
		return ResultUtils.simpleSuccess();
	}
	
	public Result batchGrid(String type, String func) {
		return ResultUtils.simpleSuccess(getBatchGrid(NOTIFICATION, type, func));
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result batchRemove(Map<String, Object> body) {
		List<Object> ids = SystemUtils.transformList(body.get("ids"));
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		NotificationService notificationService = NotificationServiceFactory.get();
		Long userId = AuthUtils.getUid();
		for (Object idObj : ids) {
			if (idObj == null) {
				continue;
			}
			Long id = Long.valueOf(idObj.toString());
			Notification notification = notificationService.findById(id.longValue()).orElse(null);
			if (notification == null || !notification.getOperatorId().equals(userId)) {
				continue;
			}
			notificationService.delete(notification);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected Header header(Object key, List<Functions> functions) {
		return Header.builder().title(messageService.getMessage("notification.management")).build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("system.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("notification.management")).active(true).build());
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("type");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("notification.type")));
		Map<String, FieldFilter> value = new HashMap<>(4);
		Map<Integer, String> notificationTypeMap = notificationTypeSelection.select();
		List<Integer> values = new ArrayList<>(notificationTypeMap.keySet());
		List<String> texts = new ArrayList<>(notificationTypeMap.values());
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getKey(), values, texts));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("status");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("notification.status")));
		value = new HashMap<>(4);
		Map<Integer, String> notificationStatusMap = notificationStatusSelection.select();
		values = new ArrayList<>(notificationStatusMap.keySet());
		texts = new ArrayList<>(notificationStatusMap.values());
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getKey(), values, texts));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList(Constant.CREATETIME);
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("create.time")));
		value = new HashMap<>(4);
		value.put(keyValues.get(0), RangeFilter.rangeFilter("", Classes.DATE.getKey(), "", Classes.DATE.getKey()));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}

	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder()
				.field("title")
				.label(messageService.getMessage("title"))
				.views(Views.TEXTAREA.getKey())
				.width(200).build());
		columns.add(Column.builder()
				.field("content")
				.label(messageService.getMessage("content"))
				.views(Views.TEXTAREA.getKey())
				.width(200).build());
		columns.add(Column.builder()
				.field(Constant.CREATETIME)
				.label(messageService.getMessage("create.time"))
				.type("date")
				.dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ")
				.dateOutputFormat("YYYY-MM-DD HH:mm:ss")
				.width(400)
				.views(Views.TEXT.getKey())
				.sortable(true)
				.orderBy("").build());
		columns.add(Column.builder()
				.field("type")
				.label(messageService.getMessage("notification.type"))
				.views(Views.TEXT.getKey())
				.width(200).build());
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
		actions.add(Action.builder()
				.key("remove")
				.icon("trash")
				.to("/notification/main/remove")
				.text(messageService.getMessage("remove"))
				.variant("outline-danger")
				.type("modal").build());
		return actions;
	}
	
	@Override
	protected List<Batch> batches(Object key, List<Functions> functions) {
		List<Batch> batches = super.batches(key, functions);
		// TODO
		
		return batches;
	}

	@Override
	protected List<Functions> functions(Object key) {
		return SUPPORT_FUNCTIONS;
	}
	
	@Override
	protected BatchOperate batchOperate(Object key, String func) {
		BatchOperate batchOperate = new BatchOperate();
		switch (func) {
		case "batchRemove":
			batchOperate.setPath("/batch/remove");
			return batchOperate;
		default:
			return null;
		}
	}
	
	public List<NotificationInfo> buildNotificationInfos(List<Notification> list) {
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyList();
		}
		List<NotificationInfo> results = new ArrayList<>(list.size());
		Map<Integer, String> notificationTypeMap = notificationTypeSelection.select();
		for (Notification notification : list) {
			NotificationInfo result = new NotificationInfo();
			result.setContent(notification.getContent());
			result.setId(notification.getId().toString());
			result.setType(notificationTypeMap.get(notification.getType()));
			result.setTitle(buildTitle(notification));
			result.setCreateTime(notification.getCreateTime());
			result.setAction(NOTIFICATION);
			results.add(result);
		}
		return results;
	}
	
	private String buildTitle(Notification notification) {
		if (NotificationStatuses.READ.getStatus().equals(notification.getStatus())) {
			return String.format("<span class='text-secondary'>%s</span>", notification.getTitle());
		} else if (NotificationStatuses.UNREAD.getStatus().equals(notification.getStatus())) {
			return String.format("<span class='font-weight-bold'>%s</span>", notification.getTitle());
		}
		return notification.getTitle();
	}
	
}
