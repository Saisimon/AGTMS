package net.saisimon.agtms.web.controller.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.UserServiceFactory;
import net.saisimon.agtms.core.service.UserService;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.controller.base.AbstractMainController;
import net.saisimon.agtms.web.dto.resp.UserInfo;

/**
 * 用户主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/user/main")
@ControllerInfo("user.management")
public class UserMainController extends AbstractMainController {
	
	public static final String USER = "user";
	private static final String USER_FILTERS = USER + "_filters";
	private static final String USER_PAGEABLE = USER + "_pageable";
	private static final List<String> FUNCTIONS = Arrays.asList(
			Functions.VIEW.getFunction()
	);
	private static final Set<String> USER_FILTER_FIELDS = new HashSet<>();
	static {
		USER_FILTER_FIELDS.add("loginName");
		USER_FILTER_FIELDS.add("nickName");
		USER_FILTER_FIELDS.add("email");
		USER_FILTER_FIELDS.add("cellphone");
		USER_FILTER_FIELDS.add("createTime");
		USER_FILTER_FIELDS.add("updateTime");
		USER_FILTER_FIELDS.add("lastLoginTime");
	}
	
	@PostMapping("/grid")
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(USER));
	}
	
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@RequestParam Map<String, Object> param, @RequestBody Map<String, Object> body) {
		FilterRequest filter = FilterRequest.build(body, USER_FILTER_FIELDS);
		FilterPageable pageable = FilterPageable.build(param);
		UserService userService = UserServiceFactory.get();
		Page<User> page = userService.findPage(filter, pageable);
		List<UserInfo> results = new ArrayList<>(page.getContent().size());
		for (User user : page.getContent()) {
			UserInfo result = buildUserResult(user);
			result.setAction(USER);
			results.add(result);
		}
		request.getSession().setAttribute(USER_FILTERS, body);
		request.getSession().setAttribute(USER_PAGEABLE, param);
		return ResultUtils.pageSuccess(results, page.getTotalElements());
	}
	
	private UserInfo buildUserResult(User user) {
		if (user == null) {
			return null;
		}
		UserInfo result = new UserInfo();
		BeanUtils.copyProperties(user, result);
		return result;
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
		List<String> keyValues = Arrays.asList("loginName", "nickName", "email", "cellphone");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("login.name", "nick.name", "email", "cellphone")));
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
		columns.add(Column.builder().field("nickName").label(getMessage("nick.name")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("email").label(getMessage("email")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("cellphone").label(getMessage("cellphone")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("lastLoginTime").label(getMessage("last.login.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("createTime").label(getMessage("create.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("updateTime").label(getMessage("update.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("action").label(getMessage("actions")).type("number").width(100).build());
		return columns;
	}
	
	@Override
	protected List<String> functions(Object key) {
		return FUNCTIONS;
	}

}
