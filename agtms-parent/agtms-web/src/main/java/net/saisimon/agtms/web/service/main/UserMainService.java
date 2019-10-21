package net.saisimon.agtms.web.service.main;

import static net.saisimon.agtms.core.constant.Constant.Param.FILTER;
import static net.saisimon.agtms.core.constant.Constant.Param.PAGEABLE;
import static net.saisimon.agtms.core.constant.Constant.Param.PARAM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import cn.hutool.core.util.NumberUtil;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.entity.UserRole;
import net.saisimon.agtms.core.domain.entity.UserToken;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
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
import net.saisimon.agtms.core.enums.NotificationTypes;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.NotificationServiceFactory;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.factory.UserRoleServiceFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.property.AccountProperties;
import net.saisimon.agtms.core.service.UserRoleService;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.resp.UserInfo;
import net.saisimon.agtms.web.selection.RoleSelection;
import net.saisimon.agtms.web.selection.UserStatusSelection;
import net.saisimon.agtms.web.service.base.AbstractMainService;

/**
 * 用户主服务
 * 
 * @author saisimon
 *
 */
@Service
public class UserMainService extends AbstractMainService {
	
	public static final String USER = "user";
	public static final List<Functions> SUPPORT_FUNCTIONS = Arrays.asList(
			Functions.VIEW,
			Functions.CREATE, 
			Functions.EDIT,
			Functions.LOCK,
			Functions.UNLOCK,
			Functions.RESET_PASSWORD,
			Functions.GRANT,
			Functions.SEND_NOTIFICATION
	);
	
	private static final String USER_FILTERS = USER + FILTER_SUFFIX;
	private static final String USER_PAGEABLE = USER + PAGEABLE_SUFFIX;
	private static final Set<String> USER_FILTER_FIELDS = new HashSet<>();
	static {
		USER_FILTER_FIELDS.add("loginName");
		USER_FILTER_FIELDS.add("nickname");
		USER_FILTER_FIELDS.add("email");
		USER_FILTER_FIELDS.add("cellphone");
		USER_FILTER_FIELDS.add("lastLoginTime");
		USER_FILTER_FIELDS.add(Constant.CREATETIME);
		USER_FILTER_FIELDS.add(Constant.UPDATETIME);
	}
	
	@Autowired
	private UserStatusSelection userStatusSelection;
	@Autowired
	private AccountProperties accountProperties;
	@Autowired
	private RoleSelection roleSelection;
	
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(USER));
	}
	
	public Result list(Map<String, Object> body) {
		Map<String, Object> filterMap = get(body, FILTER);
		Map<String, Object> pageableMap = get(body, PAGEABLE);
		FilterRequest filter = FilterRequest.build(filterMap, USER_FILTER_FIELDS);
		if (pageableMap != null) {
			pageableMap.remove(PARAM);
		}
		FilterPageable pageable = FilterPageable.build(pageableMap);
		UserService userService = UserServiceFactory.get();
		List<User> list = userService.findPage(filter, pageable, false).getContent();
		List<UserInfo> results = new ArrayList<>(list.size());
		Map<Integer, String> userStatusMap = userStatusSelection.select();
		for (User user : list) {
			UserInfo result = new UserInfo();
			result.setAvatar(user.getAvatar());
			result.setCellphone(user.getCellphone());
			result.setCreateTime(user.getCreateTime());
			result.setEmail(user.getEmail());
			result.setId(user.getId().toString());
			result.setLoginName(user.getLoginName());
			result.setNickname(user.getNickname());
			result.setUpdateTime(user.getUpdateTime());
			result.setStatus(userStatusMap.get(user.getStatus()));
			result.setAction(USER);
			boolean self = user.getId().equals(AuthUtils.getUid());
			boolean locked = UserStatuses.LOCKED.getStatus().equals(user.getStatus());
			// edit
			result.getDisableActions().add(Boolean.FALSE);
			// unlock
			result.getDisableActions().add(self || !locked);
			// lock
			result.getDisableActions().add(self || locked);
			// reset password
			result.getDisableActions().add(Boolean.FALSE);
			results.add(result);
		}
		request.getSession().setAttribute(USER_FILTERS, filterMap);
		request.getSession().setAttribute(USER_PAGEABLE, pageableMap);
		return ResultUtils.pageSuccess(results, list.size() < pageable.getSize());
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result lock(Long id) {
		UserService userService = UserServiceFactory.get();
		Optional<User> optional = userService.findById(id);
		if (!optional.isPresent()) {
			return ErrorMessage.User.ACCOUNT_NOT_EXIST;
		}
		User user = optional.get();
		user.setStatus(UserStatuses.LOCKED.getStatus());
		userService.saveOrUpdate(user);
		TokenFactory.get().setToken(user.getId(), null);
		return ResultUtils.simpleSuccess();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result unlock(Long id) {
		UserService userService = UserServiceFactory.get();
		Optional<User> optional = userService.findById(id);
		if (!optional.isPresent()) {
			return ErrorMessage.User.ACCOUNT_NOT_EXIST;
		}
		User user = optional.get();
		user.setStatus(UserStatuses.NORMAL.getStatus());
		userService.saveOrUpdate(user);
		TokenFactory.get().setToken(user.getId(), null);
		return ResultUtils.simpleSuccess();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result resetPassword(Long id) {
		UserService userService = UserServiceFactory.get();
		Optional<User> optional = userService.findById(id);
		if (!optional.isPresent()) {
			return ErrorMessage.User.ACCOUNT_NOT_EXIST;
		}
		User user = optional.get();
		String hmacPassword = AuthUtils.hmac(accountProperties.getResetPassword(), user.getSalt());
		user.setPassword(hmacPassword);
		user.setStatus(UserStatuses.CREATED.getStatus());
		userService.saveOrUpdate(user);
		TokenFactory.get().setToken(user.getId(), null);
		return ResultUtils.simpleSuccess();
	}
	
	public Result batchGrid(String type, String func) {
		return ResultUtils.simpleSuccess(getBatchGrid(USER, type, func));
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result grant(Map<String, Object> body) {
		List<Object> ids = SystemUtils.transformList(body.get("ids"));
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		List<String> rolePaths = SystemUtils.transformList(body.get("roles"));
		Map<String, String> roleMap = roleSelection.select();
		for (Object idObj : ids) {
			if (idObj == null) {
				continue;
			}
			Long id = Long.valueOf(idObj.toString());
			User user = UserServiceFactory.get().findById(id).orElse(null);
			if (user == null) {
				continue;
			}
			grantRole(user.getId(), roleMap, rolePaths);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result sendNotification(Map<String, Object> body) {
		List<Object> ids = SystemUtils.transformList(body.get("ids"));
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		String title = (String) body.get("title");
		if (SystemUtils.isBlank(title)) {
			return ErrorMessage.Common.FIELD_CONTENT_BLANK.messageArgs(messageService.getMessage("title"));
		} else if (title.length() > 64) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("title"), 64);
		}
		String content = (String) body.get("content");
		if (SystemUtils.isBlank(content)) {
			return ErrorMessage.Common.FIELD_CONTENT_BLANK.messageArgs(messageService.getMessage("content"));
		} else if (content.length() > 4096) {
			return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(messageService.getMessage("content"), 4096);
		}
		for (Object idObj : ids) {
			if (idObj == null) {
				continue;
			}
			Long id = Long.valueOf(idObj.toString());
			User user = UserServiceFactory.get().findById(id).orElse(null);
			if (user == null) {
				continue;
			}
			NotificationServiceFactory.get().sendNotification(title, content, NotificationTypes.SYSTEM_NOTICE, user.getId());
		}
		return ResultUtils.simpleSuccess();
	}
	
	public void grantRole(Long userId, Map<String, String> roleMap, List<String> rolePaths) {
		if (userId == null) {
			return;
		}
		UserRoleService userRoleService = UserRoleServiceFactory.get();
		userRoleService.delete(FilterRequest.build().and("userId", userId));
		if (roleMap == null || CollectionUtils.isEmpty(rolePaths)) {
			return;
		}
		for (String rolePath : rolePaths) {
			if (!roleMap.containsKey(rolePath)) {
				continue;
			}
			int idx = rolePath.lastIndexOf('/');
			UserRole userRole = new UserRole();
			userRole.setRoleId(Long.valueOf(rolePath.substring(idx + 1)));
			userRole.setRolePath(rolePath.substring(0, idx));
			userRole.setUserId(userId);
			userRoleService.saveOrUpdate(userRole);
		}
	}
	
	public boolean checkToken(String uid, String token) {
		if (!NumberUtil.isLong(uid)) {
			return false;
		}
		UserToken userToken = TokenFactory.get().getToken(Long.valueOf(uid), true);
		if (userToken == null) {
			return false;
		}
		return token.equals(userToken.getToken());
	}
	
	@Override
	protected Header header(Object key, List<Functions> functions) {
		Header header = Header.builder().title(messageService.getMessage("user.management")).build();
		if (SystemUtils.hasFunction(Functions.EDIT.getCode(), functions)) {
			header.setCreateUrl("/user/edit");
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
				.text(messageService.getMessage("user.management"))
				.active(true).build());
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("loginName", "nickname", "email", "cellphone");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("login.name", "nickname", "email", "cellphone")));
		Map<String, FieldFilter> value = new HashMap<>();
		value.put(keyValues.get(0), TextFilter.textFilter("", Classes.STRING.getKey(), SingleSelect.OPERATORS.get(0)));
		value.put(keyValues.get(1), TextFilter.textFilter("", Classes.STRING.getKey(), SingleSelect.OPERATORS.get(0)));
		value.put(keyValues.get(2), TextFilter.textFilter("", Classes.STRING.getKey(), SingleSelect.OPERATORS.get(0)));
		value.put(keyValues.get(3), TextFilter.textFilter("", Classes.STRING.getKey(), SingleSelect.OPERATORS.get(0)));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("lastLoginTime", Constant.CREATETIME, Constant.UPDATETIME);
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("last.login.time", "create.time", "update.time")));
		value = new HashMap<>();
		value.put(keyValues.get(0), RangeFilter.rangeFilter("", Classes.DATE.getKey(), "", Classes.DATE.getKey()));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getKey(), "", Classes.DATE.getKey()));
		value.put(keyValues.get(2), RangeFilter.rangeFilter("", Classes.DATE.getKey(), "", Classes.DATE.getKey()));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}

	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder()
				.field("loginName")
				.label(messageService.getMessage("login.name"))
				.width(200)
				.views(Views.TEXT.getKey()).build());
		columns.add(Column.builder()
				.field("nickname")
				.label(messageService.getMessage("nickname"))
				.width(200)
				.views(Views.TEXT.getKey()).build());
		columns.add(Column.builder()
				.field("email")
				.label(messageService.getMessage("email"))
				.width(200)
				.views(Views.TEXT.getKey()).build());
		columns.add(Column.builder()
				.field("cellphone")
				.label(messageService.getMessage("cellphone"))
				.width(200)
				.views(Views.TEXT.getKey()).build());
		columns.add(Column.builder()
				.field("avatar")
				.label(messageService.getMessage("avatar"))
				.width(200)
				.views(Views.IMAGE.getKey()).build());
		columns.add(Column.builder()
				.field("status")
				.label(messageService.getMessage("status"))
				.width(200)
				.views(Views.TEXT.getKey()).build());
		columns.add(Column.builder()
				.field("lastLoginTime")
				.label(messageService.getMessage("last.login.time"))
				.type("date")
				.dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ")
				.dateOutputFormat("YYYY-MM-DD HH:mm:ss")
				.width(400)
				.views(Views.TEXT.getKey())
				.sortable(true)
				.orderBy("").build());
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
					.to("/user/edit?id=")
					.icon("edit")
					.text(messageService.getMessage("edit"))
					.type("link").build());
		}
		if (SystemUtils.hasFunction(Functions.UNLOCK.getCode(), functions)) {
			actions.add(Action.builder()
					.key("unlock")
					.icon("unlock")
					.to("/user/main/unlock")
					.text(messageService.getMessage("unlock"))
					.variant("outline-warning")
					.type("modal").build());
		}
		if (SystemUtils.hasFunction(Functions.LOCK.getCode(), functions)) {
			actions.add(Action.builder()
					.key("lock")
					.icon("lock")
					.to("/user/main/lock")
					.text(messageService.getMessage("lock"))
					.variant("outline-warning")
					.type("modal").build());
		}
		if (SystemUtils.hasFunction(Functions.RESET_PASSWORD.getCode(), functions)) {
			actions.add(Action.builder()
					.key("reset")
					.icon("key")
					.to("/user/main/reset/password")
					.text(messageService.getMessage("reset.password"))
					.variant("outline-danger")
					.type("modal").build());
		}
		return actions;
	}
	
	@Override
	protected List<Functions> functions(Object key) {
		return functions("/user/main", null, SUPPORT_FUNCTIONS);
	}

	@Override
	protected BatchOperate batchOperate(Object key, String func, List<Functions> functions) {
		BatchOperate batchOperate = new BatchOperate();
		switch (func) {
		case "grant":
			if (SystemUtils.hasFunction(Functions.GRANT.getCode(), functions)) {
				batchOperate.setPath("/grant");
				batchOperate.setSize("lg");
				List<Field<?>> operateFields = new ArrayList<>(1);
				Field<Option<String>> rolesField = Field.<Option<String>>builder()
						.name("roles")
						.text(messageService.getMessage("role.name"))
						.type("select")
						.views(Views.SELECTION.getKey())
						.options(roleSelection.buildNestedOptions(null))
						.multiple(true).build();
				operateFields.add(rolesField);
				batchOperate.setOperateFields(operateFields);
				return batchOperate;
			} else {
				return null;
			}
		case "sendNotification":
			if (SystemUtils.hasFunction(Functions.SEND_NOTIFICATION.getCode(), functions)) {
				batchOperate.setPath("/send/notification");
				batchOperate.setSize("lg");
				List<Field<?>> operateFields = new ArrayList<>(2);
				Field<Option<String>> titleField = Field.<Option<String>>builder()
						.name("title")
						.text(messageService.getMessage("title"))
						.type(Classes.STRING.getKey())
						.required(true).build();
				operateFields.add(titleField);
				Field<Option<String>> contentField = Field.<Option<String>>builder()
						.name("content")
						.text(messageService.getMessage("content"))
						.type(Classes.STRING.getKey())
						.required(true)
						.views(Views.TEXTAREA.getKey()).build();
				operateFields.add(contentField);
				batchOperate.setOperateFields(operateFields);
				return batchOperate;
			} else {
				return null;
			}
		default:
			return null;
		}
	}
	
}
