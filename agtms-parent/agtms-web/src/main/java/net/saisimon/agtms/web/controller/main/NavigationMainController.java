package net.saisimon.agtms.web.controller.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.TextFilter;
import net.saisimon.agtms.core.domain.grid.BatchGrid.BatchEdit;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Field;
import net.saisimon.agtms.core.domain.grid.Filter;
import net.saisimon.agtms.core.domain.grid.MainGrid.Action;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.OperateTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.MainController;
import net.saisimon.agtms.web.dto.resp.NavigationInfo;
import net.saisimon.agtms.web.dto.resp.NavigationTree;
import net.saisimon.agtms.web.dto.resp.NavigationTree.NavigationLink;
import net.saisimon.agtms.web.selection.NavigationSelection;

/**
 * 导航主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/navigation/main")
@ControllerInfo("navigation.management")
public class NavigationMainController extends MainController {
	
	public static final String NAVIGATION = "navigation";
	private static final String NAVIGATION_FILTERS = NAVIGATION + "_filters";
	private static final String NAVIGATION_PAGEABLE = NAVIGATION + "_pageable";
	private static final List<String> FUNCTIONS = Arrays.asList(
			Functions.CREATE.getFunction(),
			Functions.EDIT.getFunction(),
			Functions.REMOVE.getFunction(),
			Functions.BATCH_EDIT.getFunction(), 
			Functions.BATCH_REMOVE.getFunction()
	);
	private static final Set<String> NAVIGATION_FILTER_FIELDS = new HashSet<>();
	static {
		NAVIGATION_FILTER_FIELDS.add("title");
		NAVIGATION_FILTER_FIELDS.add("priority");
		NAVIGATION_FILTER_FIELDS.add("createTime");
		NAVIGATION_FILTER_FIELDS.add("updateTime");
	}
	
	@Autowired
	private NavigationSelection navigationSelection;
	
	@Operate(type=OperateTypes.QUERY, value="side")
	@PostMapping("/side")
	public Result side() {
		UserInfo userInfo = AuthUtils.getUserInfo();
		if (userInfo == null) {
			return ResultUtils.simpleSuccess();
		}
		NavigationService navigationService = NavigationServiceFactory.get();
		List<NavigationTree> trees = getChildNavs(navigationService.getNavigations(userInfo.getUserId()), -1L);
		if (trees == null) {
			trees = new ArrayList<>(1);
		}
		trees.add(0, NavigationTree.SYSTEM_MODEL_TREE);
		NavigationTree root = new NavigationTree();
		root.setId(-1L);
		root.setChildrens(internationNavigationTrees(trees));
		TemplateService templateService = TemplateServiceFactory.get();
		List<Template> templates = templateService.getTemplates(-1L, userInfo.getUserId());
		if (!CollectionUtils.isEmpty(templates)) {
			List<NavigationLink> links = new ArrayList<>(templates.size());
			for (Template template : templates) {
				links.add(new NavigationLink("/management/main/" + template.getId(), template.getTitle()));
			}
			root.setLinks(links);
		}
		return ResultUtils.simpleSuccess(root);
	}
	
	@Operate(type=OperateTypes.QUERY, value="selection")
	@PostMapping("/selection")
	public Result selection(@RequestParam(name = "id", required = false) Long excludeId) {
		Map<Long, String> navigationMap = navigationSelection.selectWithParent(excludeId);
		List<Option<Long>> options = new ArrayList<>(navigationMap.size());
		for (Entry<Long, String> entry : navigationMap.entrySet()) {
			Option<Long> option = new Option<>(entry.getKey(), entry.getValue());
			options.add(option);
		}
		return ResultUtils.simpleSuccess(options);
	}
	
	@PostMapping("/grid")
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(NAVIGATION));
	}
	
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@RequestParam Map<String, Object> param, @RequestBody Map<String, Object> body) {
		FilterRequest filter = FilterRequest.build(body, NAVIGATION_FILTER_FIELDS);
		filter.and(Constant.OPERATORID, AuthUtils.getUserInfo().getUserId());
		FilterPageable pageable = FilterPageable.build(param);
		NavigationService navigationService = NavigationServiceFactory.get();
		Page<Navigation> page = navigationService.findPage(filter, pageable);
		List<NavigationInfo> results = new ArrayList<>(page.getContent().size());
		for (Navigation navigation : page.getContent()) {
			NavigationInfo result = buildNavigationResult(navigation);
			result.setIcon("<i class='fa fa-"+ navigation.getIcon() +"'></i>");
			result.setAction(NAVIGATION);
			results.add(result);
		}
		request.getSession().setAttribute(NAVIGATION_FILTERS, body);
		request.getSession().setAttribute(NAVIGATION_PAGEABLE, param);
		return ResultUtils.pageSuccess(results, page.getTotalElements());
	}
	
	@Operate(type=OperateTypes.REMOVE)
	@PostMapping("/remove")
	public Result remove(@RequestParam(name = "id") Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		long userId = AuthUtils.getUserInfo().getUserId();
		NavigationService navigationService = NavigationServiceFactory.get();
		Navigation navigation = navigationService.getNavigation(id, userId);
		if (navigation == null) {
			return ErrorMessage.Navigation.NAVIGATION_NOT_EXIST;
		}
		recursiveRemove(navigation, userId);
		return ResultUtils.simpleSuccess();
	}
	
	@PostMapping("/batch/grid")
	public Result batchGrid() {
		return ResultUtils.simpleSuccess(getBatchGrid(NAVIGATION));
	}
	
	@Operate(type=OperateTypes.EDIT)
	@PostMapping("/batch/save")
	public Result batchSave(@RequestBody Map<String, Object> body) {
		List<Long> ids = SystemUtils.transformList(body.get("ids"), Long.class);
		if (CollectionUtils.isEmpty(ids)) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		String icon = (String) body.get("icon");
		Object priorityObj = body.get("priority");
		long userId = AuthUtils.getUserInfo().getUserId();
		NavigationService navigationService = NavigationServiceFactory.get();
		List<Navigation> navigations = navigationService.getNavigations(ids, userId);
		for (Navigation navigation : navigations) {
			boolean update = false;
			if (StringUtils.isNotBlank(icon) && !icon.equals(navigation.getIcon())) {
				update = true;
				navigation.setIcon(icon);
			}
			if (priorityObj != null && !priorityObj.toString().equals(navigation.getPriority().toString())) {
				update = true;
				navigation.setPriority(Long.valueOf(priorityObj.toString()));
			}
			if (update) {
				navigation.setUpdateTime(new Date());
				navigationService.saveOrUpdate(navigation);
			}
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Operate(type=OperateTypes.BATCH_REMOVE)
	@PostMapping("/batch/remove")
	public Result batchRemove(@RequestBody List<Long> ids) {
		if (ids.size() == 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		long userId = AuthUtils.getUserInfo().getUserId();
		NavigationService navigationService = NavigationServiceFactory.get();
		List<Navigation> navigations = navigationService.getNavigations(ids, userId);
		for (Navigation navigation : navigations) {
			recursiveRemove(navigation, userId);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected Header header(Object key) {
		return Header.builder().title(getMessage("navigation.management")).createUrl("/navigation/edit").build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.model")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("navigation.management")).active(true).build());
		return breadcrumbs;
	}
	
	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("icon").label(getMessage("icon")).width(100).view(Views.ICON.getView()).build());
		columns.add(Column.builder().field("title").label(getMessage("title")).width(200).view(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("priority").label(getMessage("priority")).type("number").width(100).view(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("createTime").label(getMessage("create.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).view(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("updateTime").label(getMessage("update.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).view(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("action").label(getMessage("actions")).type("number").width(100).build());
		return columns;
	}
	
	@Override
	protected List<Action> actions(Object key) {
		List<Action> actions = new ArrayList<>();
		actions.add(Action.builder().key("template").to("/template/edit?nid=").icon("plus").text(getMessage("add.template")).variant("outline-dark").type("link").build());
		actions.add(Action.builder().key("edit").to("/navigation/edit?id=").icon("edit").text(getMessage("edit")).type("link").build());
		actions.add(Action.builder().key("remove").icon("trash").to("/navigation/main/remove").text(getMessage("remove")).variant("outline-danger").type("modal").build());
		return actions;
	}

	@Override
	protected BatchEdit batchEdit(Object key) {
		BatchEdit batchEdit = new BatchEdit();
		Option<String> parentIdOption = new Option<>("parentId", getMessage("parent.navigation"), true);
		Option<String> titleOption = new Option<>("title", getMessage("title"), true);
		Option<String> iconOption = new Option<>("icon", getMessage("icon"), false);
		Option<String> priorityOption = new Option<>("priority", getMessage("priority"), false);
		List<Option<String>> editFieldOptions = Arrays.asList(parentIdOption, titleOption, iconOption, priorityOption);
		batchEdit.setEditFieldOptions(editFieldOptions);
		Map<String, Field<?>> editFields = new HashMap<>();
		editFields.put("icon", Field.<String>builder().value(Navigation.DEFAULT_ICON).name("icon").required(true).text(getMessage("icon")).type(Classes.STRING.getName()).view("icon").build());
		editFields.put("priority", Field.<Long>builder().value(Navigation.DEFAULT_PRIORITY).name("priority").text(getMessage("priority")).type(Classes.LONG.getName()).build());
		batchEdit.setEditFields(editFields);
		return batchEdit;
	}

	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("title", "priority");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, keyValues));
		Map<String, FieldFilter> value = new HashMap<>();
		value.put("title", TextFilter.textFilter("", Classes.STRING.getName(), SingleSelect.OPERATORS.get(0)));
		value.put("priority", RangeFilter.rangeFilter("", Classes.LONG.getName(), "", Classes.LONG.getName()));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("createTime", "updateTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("create.time", "update.time")));
		value = new HashMap<>();
		value.put("createTime", RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		value.put("updateTime", RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}
	
	@Override
	protected List<String> functions(Object key) {
		return FUNCTIONS;
	}
	
	private NavigationInfo buildNavigationResult(Navigation navigation) {
		if (navigation == null) {
			return null;
		}
		NavigationInfo result = new NavigationInfo();
		BeanUtils.copyProperties(navigation, result);
		return result;
	}
	
	private void recursiveRemove(Navigation navigation, long userId) {
		NavigationService navigationService = NavigationServiceFactory.get();
		Long id = navigation.getId();
		List<Navigation> list = navigationService.getChildrenNavigations(id, userId);
		if (!CollectionUtils.isEmpty(list)) {
			for (Navigation n : list) {
				recursiveRemove(n, userId);
			}
		}
		navigationService.delete(id);
	}
	
	private List<NavigationTree> getChildNavs(List<Navigation> navigations, Long parentId) {
		if (CollectionUtils.isEmpty(navigations)) {
			return null;
		}
		List<NavigationTree> trees = new ArrayList<>();
		List<Navigation> currents = new ArrayList<>();
		List<Navigation> rests = new ArrayList<>();
		for (Navigation pn : navigations) {
			if (pn.getParentId().longValue() == parentId.longValue()) {
				currents.add(pn);
			} else {
				rests.add(pn);
			}
		}
		TemplateService templateService = TemplateServiceFactory.get();
		for (Navigation current : currents) {
			NavigationTree tree = new NavigationTree();
			tree.setId(current.getId());
			tree.setTitle(current.getTitle());
			tree.setIcon(current.getIcon());
			tree.setPriority(current.getPriority());
			List<Template> templates = templateService.getTemplates(current.getId(), current.getOperatorId());
			if (!CollectionUtils.isEmpty(templates)) {
				List<NavigationLink> links = new ArrayList<>(templates.size());
				for (Template template : templates) {
					links.add(new NavigationLink("/management/main/" + template.getId(), template.getTitle()));
				}
				tree.setLinks(links);
			}
			tree.setChildrens(getChildNavs(rests, current.getId()));
			trees.add(tree);
		}
		Collections.sort(trees, (n1, n2) -> {
			int i = n1.getPriority().compareTo(n2.getPriority());
			if (i != 0) {
				return i;
			}
			return n1.getId().compareTo(n2.getId());
		});
		return trees;
	}
	
}
