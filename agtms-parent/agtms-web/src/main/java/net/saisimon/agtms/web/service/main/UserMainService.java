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

import cn.hutool.core.util.NumberUtil;
import net.saisimon.agtms.core.domain.entity.User;
import net.saisimon.agtms.core.domain.entity.UserToken;
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
import net.saisimon.agtms.core.enums.UserStatuses;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.TokenFactory;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.resp.UserInfo;
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
			Functions.RESET_PASSWORD
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
		USER_FILTER_FIELDS.add("createTime");
		USER_FILTER_FIELDS.add("updateTime");
	}
	
	@Autowired
	private UserStatusSelection userStatusSelection;
	@Autowired
	private AgtmsProperties agtmsProperties;
	
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
		String hmacPassword = AuthUtils.hmac(agtmsProperties.getResetPassword(), user.getSalt());
		user.setPassword(hmacPassword);
		user.setStatus(UserStatuses.CREATED.getStatus());
		userService.saveOrUpdate(user);
		TokenFactory.get().setToken(user.getId(), null);
		return ResultUtils.simpleSuccess();
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
	protected Header header(Object key) {
		return Header.builder().title(messageService.getMessage("user.management")).createUrl("/user/edit").build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("system.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("user.management")).active(true).build());
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
		keyValues = Arrays.asList("lastLoginTime", "createTime", "updateTime");
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
		columns.add(Column.builder().field("loginName").label(messageService.getMessage("login.name")).width(200).views(Views.TEXT.getKey()).build());
		columns.add(Column.builder().field("nickname").label(messageService.getMessage("nickname")).width(200).views(Views.TEXT.getKey()).build());
		columns.add(Column.builder().field("email").label(messageService.getMessage("email")).width(200).views(Views.TEXT.getKey()).build());
		columns.add(Column.builder().field("cellphone").label(messageService.getMessage("cellphone")).width(200).views(Views.TEXT.getKey()).build());
		columns.add(Column.builder().field("avatar").label(messageService.getMessage("avatar")).width(200).views(Views.IMAGE.getKey()).build());
		columns.add(Column.builder().field("status").label(messageService.getMessage("status")).width(200).views(Views.TEXT.getKey()).build());
		columns.add(Column.builder().field("lastLoginTime").label(messageService.getMessage("last.login.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getKey()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("createTime").label(messageService.getMessage("create.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getKey()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("updateTime").label(messageService.getMessage("update.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getKey()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("action").label(messageService.getMessage("actions")).type("number").width(100).build());
		return columns;
	}
	
	@Override
	protected List<Action> actions(Object key) {
		List<Action> actions = new ArrayList<>();
		actions.add(Action.builder().key("edit").to("/user/edit?id=").icon("edit").text(messageService.getMessage("edit")).type("link").build());
		actions.add(Action.builder().key("unlock").icon("unlock").to("/user/main/unlock").text(messageService.getMessage("unlock")).variant("outline-warning").type("modal").build());
		actions.add(Action.builder().key("lock").icon("lock").to("/user/main/lock").text(messageService.getMessage("lock")).variant("outline-warning").type("modal").build());
		actions.add(Action.builder().key("reset").icon("key").to("/user/main/reset/password").text(messageService.getMessage("reset.password")).variant("outline-danger").type("modal").build());
		return actions;
	}
	
	@Override
	protected List<Functions> functions(Object key) {
		return SUPPORT_FUNCTIONS;
	}
	
}
