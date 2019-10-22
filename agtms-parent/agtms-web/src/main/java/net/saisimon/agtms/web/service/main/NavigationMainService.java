package net.saisimon.agtms.web.service.main;

import static net.saisimon.agtms.core.constant.Constant.Param.FILTER;
import static net.saisimon.agtms.core.constant.Constant.Param.PAGEABLE;
import static net.saisimon.agtms.core.constant.Constant.Param.PARAM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Resource;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.SelectFilter;
import net.saisimon.agtms.core.domain.filter.TextFilter;
import net.saisimon.agtms.core.domain.grid.BatchEdit;
import net.saisimon.agtms.core.domain.grid.BatchOperate;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.grid.Filter;
import net.saisimon.agtms.core.domain.grid.MainGrid.Action;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.GenerateServiceFactory;
import net.saisimon.agtms.core.factory.ResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleResourceServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.ResourceService;
import net.saisimon.agtms.core.service.RoleResourceService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.resp.NavigationInfo;
import net.saisimon.agtms.web.selection.UserSelection;
import net.saisimon.agtms.web.service.base.AbstractMainService;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 导航主服务
 * 
 * @author saisimon
 *
 */
@Service
public class NavigationMainService extends AbstractMainService {
	
	public static final String NAVIGATION = "navigation";
	public static final List<Functions> SUPPORT_FUNCTIONS = Arrays.asList(
			Functions.VIEW,
			Functions.CREATE, 
			Functions.EDIT,
			Functions.BATCH_EDIT,
			Functions.REMOVE,
			Functions.BATCH_REMOVE
	);
	
	private static final String NAVIGATION_FILTERS = NAVIGATION + FILTER_SUFFIX;
	private static final String NAVIGATION_PAGEABLE = NAVIGATION + PAGEABLE_SUFFIX;
	private static final Set<String> NAVIGATION_FILTER_FIELDS = new HashSet<>();
	static {
		NAVIGATION_FILTER_FIELDS.add("name");
		NAVIGATION_FILTER_FIELDS.add(Constant.CREATETIME);
		NAVIGATION_FILTER_FIELDS.add(Constant.UPDATETIME);
		NAVIGATION_FILTER_FIELDS.add(Constant.OPERATORID);
	}
	
	@Autowired
	private UserSelection userSelection;
	@Autowired
	private PremissionService premissionService;
	
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(NAVIGATION));
	}
	
	public Result list(Map<String, Object> body) {
		Map<String, Object> filterMap = get(body, FILTER);
		Map<String, Object> pageableMap = get(body, PAGEABLE);
		FilterRequest filter = FilterRequest.build(filterMap, NAVIGATION_FILTER_FIELDS);
		if (filter == null) {
			filter = FilterRequest.build();
		}
		filter.and(Constant.OPERATORID, premissionService.getUserIds(AuthUtils.getUid()), Constant.Operator.IN).and("contentType", Resource.ContentType.NAVIGATION.getValue());
		if (pageableMap != null) {
			pageableMap.remove(PARAM);
		}
		FilterPageable pageable = FilterPageable.build(pageableMap);
		ResourceService resourceService = ResourceServiceFactory.get();
		List<Resource> list = resourceService.findPage(filter, pageable, false).getContent();
		List<NavigationInfo> results = new ArrayList<>(list.size());
		Map<String, String> userMap = userSelection.select();
		for (Resource resource : list) {
			NavigationInfo result = new NavigationInfo();
			result.setId(resource.getId().toString());
			result.setCreateTime(resource.getCreateTime());
			result.setUpdateTime(resource.getUpdateTime());
			result.setIcon(resource.getIcon());
			result.setName(resource.getName());
			result.setOperator(userMap.get(resource.getOperatorId().toString()));
			result.setAction(NAVIGATION);
			results.add(result);
		}
		HttpSession session = request.getSession();
		session.setAttribute(NAVIGATION_FILTERS, filterMap);
		session.setAttribute(NAVIGATION_PAGEABLE, pageableMap);
		return ResultUtils.pageSuccess(results, list.size() < pageable.getSize());
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result remove(Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		ResourceService resourceService = ResourceServiceFactory.get();
		Resource resource = resourceService.findById(id.longValue()).orElse(null);
		if (resource == null) {
			return ErrorMessage.Navigation.NAVIGATION_NOT_EXIST;
		}
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		if (!userIds.contains(resource.getOperatorId())) {
			return ErrorMessage.Navigation.NAVIGATION_NOT_EXIST;
		}
		recursiveRemove(resource);
		return ResultUtils.simpleSuccess();
	}
	
	public Result batchGrid(String type, String func) {
		return ResultUtils.simpleSuccess(getBatchGrid(NAVIGATION, type, func));
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result batchSave(Map<String, Object> body) {
		List<Object> ids = SystemUtils.transformList(body.get("ids"));
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		String icon = (String) body.get("icon");
		ResourceService resourceService = ResourceServiceFactory.get();
		for (Object idObj : ids) {
			if (idObj == null) {
				continue;
			}
			Long id = Long.valueOf(idObj.toString());
			Resource resource = resourceService.findById(id).orElse(null);
			if (resource == null || !userIds.contains(resource.getOperatorId())) {
				continue;
			}
			boolean update = false;
			if (SystemUtils.isNotBlank(icon) && !icon.equals(resource.getIcon())) {
				update = true;
				resource.setIcon(icon);
			}
			if (update) {
				resource.setUpdateTime(new Date());
				resourceService.saveOrUpdate(resource);
			}
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result batchRemove(Map<String, Object> body) {
		List<Object> ids = SystemUtils.transformList(body.get("ids"));
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		ResourceService resourceService = ResourceServiceFactory.get();
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		for (Object idObj : ids) {
			if (idObj == null) {
				continue;
			}
			Long id = Long.valueOf(idObj.toString());
			Resource resource = resourceService.findById(id).orElse(null);
			if (resource == null || !userIds.contains(resource.getOperatorId())) {
				continue;
			}
			recursiveRemove(resource);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected Header header(Object key, List<Functions> functions) {
		Header header = Header.builder().title(messageService.getMessage("navigation.management")).build();
		if (SystemUtils.hasFunction(Functions.CREATE.getCode(), functions)) {
			header.setCreateUrl("/navigation/edit");
		}
		return header;
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage("system.module"))
				.to("/").build());
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage("navigation.management"))
				.active(true).build());
		return breadcrumbs;
	}
	
	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder()
				.field("icon")
				.label(messageService.getMessage("icon"))
				.width(100)
				.views(Views.ICON.getKey()).build());
		columns.add(Column.builder()
				.field("name")
				.label(messageService.getMessage("title"))
				.width(200)
				.views(Views.TEXT.getKey()).build());
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
				.field(Constant.UPDATETIME)
				.label(messageService.getMessage("update.time"))
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
		if (SystemUtils.hasFunction(Functions.EDIT.getCode(), functions)) {
			actions.add(Action.builder()
					.key(Functions.EDIT.getFunction())
					.to("/navigation/edit?id=")
					.icon("edit")
					.text(messageService.getMessage(Functions.EDIT.getFunction()))
					.type("link").build());
		}
		if (SystemUtils.hasFunction(Functions.REMOVE.getCode(), functions)) {
			actions.add(Action.builder()
					.key(Functions.REMOVE.getFunction())
					.icon("trash")
					.to("/navigation/main/remove")
					.text(messageService.getMessage(Functions.REMOVE.getFunction()))
					.variant("outline-danger")
					.type("modal").build());
		}
		return actions;
	}

	@Override
	protected BatchEdit batchEdit(Object key, List<Functions> functions) {
		if (!SystemUtils.hasFunction(Functions.BATCH_EDIT.getCode(), functions)) {
			return null;
		}
		BatchEdit batchEdit = new BatchEdit();
		Option<String> pathOption = new Option<>("path", messageService.getMessage("parent.navigation"), true);
		Option<String> nameOption = new Option<>("name", messageService.getMessage("title"), true);
		Option<String> iconOption = new Option<>("icon", messageService.getMessage("icon"), false);
		List<Option<String>> editFieldOptions = Arrays.asList(pathOption, nameOption, iconOption);
		batchEdit.setEditFieldOptions(editFieldOptions);
		Map<String, Field<?>> editFields = new HashMap<>();
		editFields.put("icon", Field.<String>builder().value(Resource.DEFAULT_ICON).name("icon").required(true).text(messageService.getMessage("icon")).type(Classes.STRING.getKey()).views(Views.ICON.getKey()).build());
		batchEdit.setEditFields(editFields);
		return batchEdit;
	}

	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("name");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("title")));
		Map<String, FieldFilter> value = new HashMap<>(4);
		value.put(keyValues.get(0), TextFilter.textFilter("", Classes.STRING.getKey(), SingleSelect.OPERATORS.get(0)));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList(Constant.CREATETIME, Constant.UPDATETIME);
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("create.time", "update.time")));
		value = new HashMap<>(4);
		value.put(keyValues.get(0), RangeFilter.rangeFilter("", Classes.DATE.getKey(), "", Classes.DATE.getKey()));
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
	protected List<Functions> functions(Object key) {
		return functions("/navigation/main", null, SUPPORT_FUNCTIONS);
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
	
	private void recursiveRemove(Resource resource) {
		Set<Long> resourceIds = new HashSet<>();
		ResourceService resourceService = ResourceServiceFactory.get();
		List<Resource> childrens = resourceService.getAllChildrenResources(resource.getId(), resource.getPath());
		for (Resource children : childrens) {
			if (Resource.ContentType.TEMPLATE.getValue().equals(children.getContentType())) {
				removeTemplates(children);
			}
			resourceService.delete(children);
			resourceIds.add(children.getId());
		}
		resourceService.delete(resource);
		resourceIds.add(resource.getId());
		removeRoleResource(resourceIds);
	}
	
	private void removeTemplates(Resource resource) {
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.findById(resource.getContentId()).orElse(null);
		if (template != null) {
			GenerateServiceFactory.build(template).dropTable();
			templateService.delete(template);
		}
	}
	
	private void removeRoleResource(Collection<Long> resourceIds) {
		RoleResourceService roleResourceService = RoleResourceServiceFactory.get();
		roleResourceService.delete(FilterRequest.build().and("resourceId", resourceIds, Constant.Operator.IN));
	}
	
}
