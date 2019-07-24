package net.saisimon.agtms.web.controller.main;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.Admin;
import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.TextFilter;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Filter;
import net.saisimon.agtms.core.domain.grid.MainGrid.Action;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.AbstractMainController;
import net.saisimon.agtms.web.dto.resp.UserInfo;
import net.saisimon.agtms.web.selection.UserStatusSelection;

/**
 * 用户主控制器
 * 
 * @author saisimon
 *
 */
@Admin
@RestController
@RequestMapping("/user/main")
@ControllerInfo("user.management")
public class UserMainController extends AbstractMainController {
	
	public static final String USER = "user";
	private static final String USER_FILTERS = USER + FILTER_SUFFIX;
	private static final String USER_PAGEABLE = USER + PAGEABLE_SUFFIX;
	private static final List<String> FUNCTIONS = Arrays.asList(
			Functions.VIEW.getFunction(),
			Functions.CREATE.getFunction(),
			Functions.EDIT.getFunction()
	);
	private static final Set<String> USER_FILTER_FIELDS = new HashSet<>();
	static {
		USER_FILTER_FIELDS.add("loginName");
		USER_FILTER_FIELDS.add("nickname");
		USER_FILTER_FIELDS.add("email");
		USER_FILTER_FIELDS.add("cellphone");
		USER_FILTER_FIELDS.add("lastLoginTime");
		USER_FILTER_FIELDS.add("createTime");
		USER_FILTER_FIELDS.add("updateTime");
	}
	
	@Autowired
	private UserStatusSelection userStatusSelection;
	@Autowired
	private AgtmsProperties agtmsProperties;
	
	@PostMapping("/grid")
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(USER));
	}
	
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@RequestBody Map<String, Object> body) {
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
			if (user.getId().equals(AuthUtils.getUid())) {
				// edit
				result.getDisableActions().add(Boolean.FALSE);
				// unlock
				result.getDisableActions().add(Boolean.TRUE);
				// lock
				result.getDisableActions().add(Boolean.TRUE);
				// reset password
				result.getDisableActions().add(Boolean.FALSE);
			} else {
				// edit
				result.getDisableActions().add(Boolean.FALSE);
				if (UserStatuses.LOCKED.getStatus().equals(user.getStatus())) {
					// unlock
					result.getDisableActions().add(Boolean.FALSE);
					// lock
					result.getDisableActions().add(Boolean.TRUE);
				} else {
					// unlock
					result.getDisableActions().add(Boolean.TRUE);
					// lock
					result.getDisableActions().add(Boolean.FALSE);
				}
				// reset password
				result.getDisableActions().add(Boolean.FALSE);
			}
			results.add(result);
		}
		request.getSession().setAttribute(USER_FILTERS, filterMap);
		request.getSession().setAttribute(USER_PAGEABLE, pageableMap);
		return ResultUtils.pageSuccess(results, list.size() < pageable.getSize());
	}
	
	@Operate(type=OperateTypes.EDIT, value="lock")
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/lock")
	public Result lock(@RequestParam("id") Long id) {
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
	
	@Operate(type=OperateTypes.EDIT, value="unlock")
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/unlock")
	public Result unlock(@RequestParam("id") Long id) {
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
	
	@Operate(type=OperateTypes.EDIT, value="reset.password")
	@Transactional(rollbackOn = Exception.class)
	@PostMapping("/reset/password")
	public Result resetPassword(@RequestParam("id") Long id) {
		UserService userService = UserServiceFactory.get();
		Optional<User> optional = userService.findById(id);
		if (!optional.isPresent()) {
			return ErrorMessage.User.ACCOUNT_NOT_EXIST;
		}
		User user = optional.get();
		String hmacPassword = AuthUtils.hmac(agtmsProperties.getResetPassword(), user.getSalt());
		user.setPassword(hmacPassword);
		user.setStatus(UserStatuses.CREATED.getStatus());
		userService.saveOrUpdate(user);
		TokenFactory.get().setToken(user.getId(), null);
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected Header header(Object key) {
		return Header.builder().title(getMessage("user.management")).createUrl("/user/edit").build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("user.management")).active(true).build());
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("loginName", "nickname", "email", "cellphone");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("login.name", "nickname", "email", "cellphone")));
		Map<String, FieldFilter> value = new HashMap<>();
		value.put(keyValues.get(0), TextFilter.textFilter("", Classes.STRING.getName(), SingleSelect.OPERATORS.get(0)));
		value.put(keyValues.get(1), TextFilter.textFilter("", Classes.STRING.getName(), SingleSelect.OPERATORS.get(0)));
		value.put(keyValues.get(2), TextFilter.textFilter("", Classes.STRING.getName(), SingleSelect.OPERATORS.get(0)));
		value.put(keyValues.get(3), TextFilter.textFilter("", Classes.STRING.getName(), SingleSelect.OPERATORS.get(0)));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("lastLoginTime", "createTime", "updateTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("last.login.time", "create.time", "update.time")));
		value = new HashMap<>();
		value.put(keyValues.get(0), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		value.put(keyValues.get(2), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}

	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("loginName").label(getMessage("login.name")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("nickname").label(getMessage("nickname")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("email").label(getMessage("email")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("cellphone").label(getMessage("cellphone")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("avatar").label(getMessage("avatar")).width(200).views(Views.IMAGE.getView()).build());
		columns.add(Column.builder().field("status").label(getMessage("status")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("lastLoginTime").label(getMessage("last.login.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("createTime").label(getMessage("create.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("updateTime").label(getMessage("update.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("action").label(getMessage("actions")).type("number").width(100).build());
		return columns;
	}
	
	@Override
	protected List<Action> actions(Object key) {
		List<Action> actions = new ArrayList<>();
		actions.add(Action.builder().key("edit").to("/user/edit?id=").icon("edit").text(getMessage("edit")).type("link").build());
		actions.add(Action.builder().key("unlock").icon("unlock").to("/user/main/unlock").text(getMessage("unlock")).variant("outline-warning").type("modal").build());
		actions.add(Action.builder().key("lock").icon("lock").to("/user/main/lock").text(getMessage("lock")).variant("outline-warning").type("modal").build());
		actions.add(Action.builder().key("reset").icon("key").to("/user/main/reset/password").text(getMessage("reset.password")).variant("outline-danger").type("modal").build());
		return actions;
	}
	
	@Override
	protected List<String> functions(Object key) {
		return FUNCTIONS;
	}

}
