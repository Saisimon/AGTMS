package net.saisimon.agtms.web.service.main;

import static net.saisimon.agtms.core.constant.Constant.Param.FILTER;
import static net.saisimon.agtms.core.constant.Constant.Param.PAGEABLE;
import static net.saisimon.agtms.core.constant.Constant.Param.PARAM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import net.saisimon.agtms.core.domain.entity.Role;
import net.saisimon.agtms.core.domain.entity.RoleResource;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.SelectFilter;
import net.saisimon.agtms.core.domain.filter.TextFilter;
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
import net.saisimon.agtms.core.factory.RoleResourceServiceFactory;
import net.saisimon.agtms.core.factory.RoleServiceFactory;
import net.saisimon.agtms.core.service.RoleResourceService;
import net.saisimon.agtms.core.service.RoleService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.resp.RoleInfo;
import net.saisimon.agtms.web.selection.ResourceSelection;
import net.saisimon.agtms.web.selection.UserSelection;
import net.saisimon.agtms.web.service.base.AbstractMainService;
import net.saisimon.agtms.web.service.common.MessageService;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 角色主服务
 * 
 * @author saisimon
 *
 */
@Service
public class RoleMainService extends AbstractMainService {
	
	@Autowired
	private ResourceSelection resourceSelection;
	
	public static final String ROLE = "role";
	public static final List<Functions> SUPPORT_FUNCTIONS = Arrays.asList(
			Functions.VIEW,
			Functions.CREATE, 
			Functions.EDIT,
			Functions.REMOVE,
			Functions.BATCH_REMOVE,
			Functions.GRANT
	);
	
	private static final String ROLE_FILTERS = ROLE + FILTER_SUFFIX;
	private static final String ROLE_PAGEABLE = ROLE + PAGEABLE_SUFFIX;
	private static final Set<String> ROLE_FILTER_FIELDS = new HashSet<>();
	static {
		ROLE_FILTER_FIELDS.add("name");
		ROLE_FILTER_FIELDS.add(Constant.CREATETIME);
		ROLE_FILTER_FIELDS.add(Constant.UPDATETIME);
		ROLE_FILTER_FIELDS.add(Constant.OPERATORID);
	}
	
	@Autowired
	private UserSelection userSelection;
	@Autowired
	private PremissionService premissionService;
	@Autowired
	private MessageService messageService;
	
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(ROLE));
	}
	
	public Result list(Map<String, Object> body) {
		Map<String, Object> filterMap = get(body, FILTER);
		Map<String, Object> pageableMap = get(body, PAGEABLE);
		FilterRequest filter = FilterRequest.build(filterMap, ROLE_FILTER_FIELDS);
		if (filter == null) {
			filter = FilterRequest.build();
		}
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		filter.and(Constant.OPERATORID, userIds, Constant.Operator.IN);
		if (pageableMap != null) {
			pageableMap.remove(PARAM);
		}
		FilterPageable pageable = FilterPageable.build(pageableMap);
		RoleService roleService = RoleServiceFactory.get();
		List<Role> list = roleService.findPage(filter, pageable, false).getContent();
		List<RoleInfo> results = new ArrayList<>(list.size());
		Map<String, String> userMap = userSelection.select();
		for (Role role : list) {
			RoleInfo result = new RoleInfo();
			result.setId(role.getId().toString());
			result.setCreateTime(role.getCreateTime());
			result.setUpdateTime(role.getUpdateTime());
			result.setName(messageService.getMessage(role.getName()));
			if (Constant.SYSTEM_OPERATORID.equals(role.getOperatorId())) {
				result.setOperator(messageService.getMessage("system"));
				// edit
				result.getDisableActions().add(Constant.SYSTEM_OPERATORID.equals(role.getOperatorId()));
				// remove
				result.getDisableActions().add(Constant.SYSTEM_OPERATORID.equals(role.getOperatorId()));
			} else {
				result.setOperator(userMap.get(role.getOperatorId().toString()));
			}
			result.setAction(ROLE);
			results.add(result);
		}
		HttpSession session = request.getSession();
		session.setAttribute(ROLE_FILTERS, filterMap);
		session.setAttribute(ROLE_PAGEABLE, pageableMap);
		return ResultUtils.pageSuccess(results, list.size() < pageable.getSize());
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result remove(Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		RoleService roleService = RoleServiceFactory.get();
		Role role = roleService.findById(id).orElse(null);
		if (role == null || Constant.SYSTEM_OPERATORID.equals(role.getOperatorId())) {
			return ErrorMessage.Role.ROLE_NOT_EXIST;
		}
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		if (!userIds.contains(role.getOperatorId())) {
			return ErrorMessage.Role.ROLE_NOT_EXIST;
		}
		recursiveRemove(role);
		return ResultUtils.simpleSuccess();
	}
	
	public Result batchGrid(String type, String func) {
		return ResultUtils.simpleSuccess(getBatchGrid(ROLE, type, func));
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result batchRemove(Map<String, Object> body) {
		List<Object> ids = SystemUtils.transformList(body.get("ids"));
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		RoleService roleService = RoleServiceFactory.get();
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		for (Object idObj : ids) {
			if (idObj == null) {
				continue;
			}
			Long id = Long.valueOf(idObj.toString());
			Role role = roleService.findById(id).orElse(null);
			if (role == null || !userIds.contains(role.getOperatorId()) || Constant.SYSTEM_OPERATORID.equals(role.getOperatorId())) {
				continue;
			}
			recursiveRemove(role);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result grant(Map<String, Object> body) {
		List<Object> ids = SystemUtils.transformList(body.get("ids"));
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		List<String> resourcePaths = SystemUtils.transformList(body.get("resources"));
		Map<String, String> resourceMap = resourceSelection.select();
		for (Object idObj : ids) {
			if (idObj == null) {
				continue;
			}
			Long id = Long.valueOf(idObj.toString());
			Role role = RoleServiceFactory.get().findById(id).orElse(null);
			if (role == null || Constant.SYSTEM_OPERATORID.equals(role.getOperatorId())) {
				continue;
			}
			grantResource(role.getId(), resourceMap, resourcePaths);
		}
		return ResultUtils.simpleSuccess();
	}
	
	public void grantResource(Long roleId, Map<String, String> resourceMap, List<String> resourcePaths) {
		if (roleId == null) {
			return;
		}
		RoleResourceService roleResourceService = RoleResourceServiceFactory.get();
		roleResourceService.delete(FilterRequest.build().and("roleId", roleId));
		if (resourceMap == null || CollectionUtils.isEmpty(resourcePaths)) {
			return;
		}
		Set<String> resourceIdStrSet = new HashSet<>();
		Map<Long, RoleResource> roleResourceMap = new HashMap<>();
		for (String resourcePath : resourcePaths) {
			if (!resourceMap.containsKey(resourcePath) || SystemUtils.isBlank(resourcePath)) {
				continue;
			}
			String[] resourceIdStrArray = resourcePath.split("/");
			String path = "";
			for (String resourceIdStr : resourceIdStrArray) {
				if (SystemUtils.isBlank(resourceIdStr)) {
					continue;
				}
				if (resourceIdStrSet.contains(resourceIdStr)) {
					path += "/" + resourceIdStr;
					continue;
				}
				resourceIdStrSet.add(resourceIdStr);
				if (resourceIdStr.indexOf(':') != -1) {
					String[] arr = resourceIdStr.split(":");
					Long resourceId = Long.valueOf(arr[0]);
					RoleResource roleResource = roleResourceMap.get(resourceId);
					if (roleResource == null) {
						roleResource = new RoleResource();
						roleResource.setRoleId(roleId);
						roleResource.setResourceId(resourceId);
						roleResource.setResourcePath(path);
						roleResource.setResourceFunctions(0);
						roleResourceMap.put(roleResource.getResourceId(), roleResource);
						resourceIdStrSet.add(arr[0]);
						path += "/" + resourceId;
					}
					roleResource.setResourceFunctions(roleResource.getResourceFunctions() + Integer.valueOf(arr[1]));
				} else {
					RoleResource roleResource = new RoleResource();
					roleResource.setRoleId(roleId);
					roleResource.setResourceId(Long.valueOf(resourceIdStr));
					roleResource.setResourcePath(path);
					roleResource.setResourceFunctions(0);
					roleResourceMap.put(roleResource.getResourceId(), roleResource);
					path += "/" + resourceIdStr;
				}
			}
		}
		for (RoleResource roleResource : roleResourceMap.values()) {
			roleResourceService.saveOrUpdate(roleResource);
		}
	}
	
	@Override
	protected Header header(Object key, List<Functions> functions) {
		Header header = Header.builder().title(messageService.getMessage("role.management")).build();
		if (SystemUtils.hasFunction(Functions.CREATE.getCode(), functions)) {
			header.setCreateUrl("/role/edit");
		}
		return header;
	}
	
	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage("user.module"))
				.to("/").build());
		breadcrumbs.add(Breadcrumb.builder()
				.text(messageService.getMessage("role.management"))
				.active(true).build());
		return breadcrumbs;
	}
	
	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("name");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("role.name")));
		Map<String, FieldFilter> value = new HashMap<>();
		value.put(keyValues.get(0), TextFilter.textFilter("", Classes.STRING.getKey(), SingleSelect.OPERATORS.get(0)));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList(Constant.CREATETIME, Constant.UPDATETIME);
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("create.time", "update.time")));
		value = new HashMap<>();
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
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder()
				.field("name")
				.label(messageService.getMessage("role.name"))
				.width(200)
				.views(Views.TEXT.getKey()).build());
		columns.add(Column.builder().field(Constant.CREATETIME)
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
					.key("edit")
					.to("/role/edit?id=")
					.icon("edit")
					.text(messageService.getMessage("edit"))
					.type("link").build());
		}
		if (SystemUtils.hasFunction(Functions.REMOVE.getCode(), functions)) {
			actions.add(Action.builder()
					.key("remove")
					.icon("trash")
					.to("/role/main/remove")
					.text(messageService.getMessage("remove"))
					.variant("outline-danger")
					.type("modal").build());
		}
		return actions;
	}
	
	@Override
	protected List<Functions> functions(Object key) {
		return functions("/role/main", null, SUPPORT_FUNCTIONS);
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
		case "grant":
			if (SystemUtils.hasFunction(Functions.GRANT.getCode(), functions)) {
				batchOperate.setPath("/grant");
				batchOperate.setSize("lg");
				List<Field<?>> operateFields = new ArrayList<>(1);
				Field<Option<String>> resourcesField = Field.<Option<String>>builder()
						.name("resources")
						.text(messageService.getMessage("resource.name"))
						.type("select")
						.views(Views.SELECTION.getKey())
						.options(resourceSelection.buildNestedOptions(null, null, true))
						.multiple(true).build();
				operateFields.add(resourcesField);
				batchOperate.setOperateFields(operateFields);
				return batchOperate;
			} else {
				return null;
			}
		default:
			return null;
		}
	}
	
	private void recursiveRemove(Role role) {
		Set<Long> roleIds = new HashSet<>();
		RoleService roleService = RoleServiceFactory.get();
		List<Role> childrens = roleService.getAllChildrenRoles(role.getId(), role.getPath());
		for (Role children : childrens) {
			roleService.delete(children);
			roleIds.add(children.getId());
		}
		roleService.delete(role);
		roleIds.add(role.getId());
		removeRoleResource(roleIds);
	}
	
	private void removeRoleResource(Collection<Long> roleIds) {
		RoleResourceService roleResourceService = RoleResourceServiceFactory.get();
		roleResourceService.delete(FilterRequest.build().and("roleId", roleIds, Constant.Operator.IN));
	}
	
}
