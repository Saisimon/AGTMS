package net.saisimon.agtms.web.controller.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.date.DateUtil;
import net.saisimon.agtms.core.domain.Navigation;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.Filter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.TextFilter;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.grid.MainGrid.Title;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.dto.UserInfo;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.NavigationServiceFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.NavigationService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.TokenUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.MainController;
import net.saisimon.agtms.web.dto.req.NavigationBatchParam;
import net.saisimon.agtms.web.dto.resp.NavigationInfo;
import net.saisimon.agtms.web.dto.resp.NavigationTree;
import net.saisimon.agtms.web.dto.resp.common.Result;
import net.saisimon.agtms.web.selection.NavigationSelection;
import net.saisimon.agtms.web.util.ResultUtils;

@RestController
@RequestMapping("/navigate/main")
public class NavigationMainController extends MainController {
	
	private static final String NAVIGATE_FILTERS = "navigate_filters";
	private static final String NAVIGATE_PAGEABLE = "navigate_pageable";
	private static final List<String> FUNCTIONS = Arrays.asList(
			Functions.CREATE.getFunction(),
			Functions.EDIT.getFunction(),
			Functions.REMOVE.getFunction(),
			Functions.BATCH_EDIT.getFunction(), 
			Functions.BATCH_REMOVE.getFunction()
	);
	
	@Autowired
	private NavigationSelection navigationSelection;
	
	@PostMapping("/grid")
	public Result grid() {
		return ResultUtils.success(getMainGrid("navigate"));
	}
	
	@PostMapping("/list")
	public Result list(@RequestParam Map<String, Object> params, @RequestBody Map<String, Object> map) {
		FilterRequest filter = FilterRequest.build(map);
		filter.and("belong", TokenUtils.getUserInfo().getUserId());
		FilterPageable pageable = FilterPageable.build(params);
		NavigationService navigationService = NavigationServiceFactory.get();
		Page<Navigation> page = navigationService.findPage(filter, pageable);
		List<NavigationInfo> results = new ArrayList<>(page.getContent().size());
		for (Navigation navigation : page.getContent()) {
			NavigationInfo result = buildNavigationResult(navigation);
			result.setIcon("<i class='fa fa-"+ navigation.getIcon() +"'></i>");
			result.setAction("navigate");
			results.add(result);
		}
		request.getSession().setAttribute(NAVIGATE_FILTERS, map);
		request.getSession().setAttribute(NAVIGATE_PAGEABLE, params);
		return ResultUtils.pageSuccess(results, page.getTotalElements());
	}
	
	@PostMapping("/side")
	public Result side() {
		UserInfo userInfo = TokenUtils.getUserInfo();
		if (userInfo == null) {
			return ResultUtils.success();
		}
		NavigationService navigationService = NavigationServiceFactory.get();
		List<NavigationTree> trees = getChildNavs(navigationService.getNavigations(userInfo.getUserId()), -1L);
		if (trees == null) {
			trees = new ArrayList<>(1);
		}
		trees.add(0, NavigationTree.SYSTEM_MODEL_TREE);
		return ResultUtils.success(internationNavigationTrees(trees));
	}
	
	@PostMapping("/selection")
	public Result selection(@RequestParam(name = "id", required = false) Long excludeId) {
		Map<Long, String> navigationMap = navigationSelection.selectWithParent(excludeId);
		List<Option<Long>> options = new ArrayList<>(navigationMap.size());
		for (Entry<Long, String> entry : navigationMap.entrySet()) {
			Option<Long> option = new Option<>(entry.getKey(), entry.getValue());
			options.add(option);
		}
		return ResultUtils.success(options);
	}
	
	@PostMapping("/batch/save")
	public Result batchSave(@Validated @RequestBody NavigationBatchParam param, BindingResult result) {
		if (result.hasErrors()) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		long userId = TokenUtils.getUserInfo().getUserId();
		NavigationService navigationService = NavigationServiceFactory.get();
		List<Navigation> navigations = navigationService.getNavigations(param.getIds(), userId);
		for (Navigation navigation : navigations) {
			boolean update = false;
			if (StringUtils.isNotBlank(param.getIcon()) && param.getIcon().equals(navigation.getIcon())) {
				update = true;
				navigation.setIcon(param.getIcon());
			}
			if (param.getPriority() != null && param.getPriority() != navigation.getPriority()) {
				update = true;
				navigation.setPriority(param.getPriority());
			}
			if (update) {
				navigation.setUpdateTime(DateUtil.formatDateTime(new Date()));
				navigationService.saveOrUpdate(navigation);
			}
		}
		return ResultUtils.success();
	}
	
	@PostMapping("/remove")
	public Result remove(@RequestParam(name = "id") Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		long userId = TokenUtils.getUserInfo().getUserId();
		NavigationService navigationService = NavigationServiceFactory.get();
		Navigation navigation = navigationService.getNavigation(id, userId);
		if (navigation == null) {
			return ErrorMessage.Navigation.NAVIGATION_NOT_EXIST;
		}
		recursiveRemove(navigation, userId);
		return ResultUtils.success();
	}
	
	@PostMapping("/batch/remove")
	public Result batchRemove(@RequestBody List<Long> ids) {
		if (ids.size() == 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		long userId = TokenUtils.getUserInfo().getUserId();
		NavigationService navigationService = NavigationServiceFactory.get();
		List<Navigation> navigations = navigationService.getNavigations(ids, userId);
		for (Navigation navigation : navigations) {
			recursiveRemove(navigation, userId);
		}
		return ResultUtils.success();
	}
	
	@Override
	protected Header header(Object key) {
		return Header.builder().title(getMessage("navigate.management")).createUrl("/navigate/edit").build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.model")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("navigate.management")).active(true).build());
		return breadcrumbs;
	}
	
	@Override
	protected List<List<Title>> titles(Object key) {
		List<List<Title>> titles = new ArrayList<>();
		List<Title> firstTitle = new ArrayList<>();
		firstTitle.add(Title.builder().fields(Arrays.asList("icon")).title(getMessage("icon")).rowspan(2).build());
		firstTitle.add(Title.builder().fields(Arrays.asList("title")).title(getMessage("title")).rowspan(2).build());
		firstTitle.add(Title.builder().fields(Arrays.asList("priority")).title(getMessage("priority")).rowspan(2).orderBy("").build());
		firstTitle.add(Title.builder().fields(Arrays.asList("createTime")).title(getMessage("create.time")).rowspan(2).orderBy("").build());
		firstTitle.add(Title.builder().fields(Arrays.asList("updateTime")).title(getMessage("update.time")).rowspan(2).orderBy("").build());
		titles.add(firstTitle);
		return titles;
	}

	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("icon").title(getMessage("icon")).width(100).view(Views.ICON.getView()).build());
		columns.add(Column.builder().field("title").title(getMessage("title")).width(200).view(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("priority").title(getMessage("priority")).width(100).view(Views.TEXT.getView()).orderBy("").build());
		columns.add(Column.builder().field("createTime").title(getMessage("create.time")).width(400).view(Views.TEXT.getView()).orderBy("").build());
		columns.add(Column.builder().field("updateTime").title(getMessage("update.time")).width(400).view(Views.TEXT.getView()).orderBy("").build());
		return columns;
	}
	
	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("title", "priority");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, keyValues));
		Map<String, FieldFilter> value = new HashMap<>();
		value.put("title", TextFilter.textFilter("", "text", "strict"));
		value.put("priority", RangeFilter.rangeFilter("", "number", "", "number"));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("createTime", "updateTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("create.time", "update.time")));
		value = new HashMap<>();
		value.put("createTime", RangeFilter.rangeFilter("", "date", "", "date"));
		value.put("updateTime", RangeFilter.rangeFilter("", "date", "", "date"));
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
			List<Template> templates = templateService.getTemplates(current.getId(), current.getBelong());
			if (!CollectionUtils.isEmpty(templates)) {
				Map<String, String> map = new HashMap<>();
				for (Template template : templates) {
					map.put("/manage/main/" + template.getId(), template.getTitle());
				}
				tree.setLinkMap(map);
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
