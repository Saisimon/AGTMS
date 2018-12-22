package net.saisimon.agtms.web.controller.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.Filter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.SelectFilter;
import net.saisimon.agtms.core.domain.filter.TextFilter;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.grid.MainGrid.Title;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TokenUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.MainController;
import net.saisimon.agtms.web.dto.resp.TemplateInfo;
import net.saisimon.agtms.web.dto.resp.common.Result;
import net.saisimon.agtms.web.selection.NavigationSelection;
import net.saisimon.agtms.web.util.ResultUtils;

@RestController
@RequestMapping("/template/main")
public class TemplateMainController extends MainController {
	
	private static final String TEMPLATE_FILTERS = "template_filters";
	private static final String TEMPLATE_PAGEABLE = "template_pageable";
	private static final List<String> FUNCTIONS = Arrays.asList(
			Functions.CREATE.getFunction(),
			"view",
			Functions.EDIT.getFunction(),
			Functions.REMOVE.getFunction(),
			Functions.BATCH_REMOVE.getFunction()
	);
	
	@Autowired
	private NavigationSelection navigationSelection;
	
	@PostMapping("/grid")
	public Result grid() {
		return ResultUtils.success(getMainGrid("template"));
	}
	
	@PostMapping("/list")
	public Result list(@RequestParam Map<String, Object> params, @RequestBody Map<String, Object> map) {
		FilterRequest filter = FilterRequest.build(map);
		filter.and("userId", TokenUtils.getUserInfo().getUserId());
		FilterPageable pageable = FilterPageable.build(params);
		TemplateService templateService = TemplateServiceFactory.get();
		Page<Template> page = templateService.findPage(filter, pageable);
		List<TemplateInfo> results = new ArrayList<>(page.getContent().size());
		Map<String, String> navigationMap = navigationSelection.select();
		for (Template template : page.getContent()) {
			TemplateInfo result = buildTemplateInfo(template);
			result.setNavigationName(navigationMap.get(template.getNavigationId().toString()));
			result.setAction("template");
			results.add(result);
		}
		request.getSession().setAttribute(TEMPLATE_FILTERS, map);
		request.getSession().setAttribute(TEMPLATE_PAGEABLE, params);
		return ResultUtils.pageSuccess(results, page.getTotalElements());
	}
	
	@PostMapping("/remove")
	public Result remove(@RequestParam(name = "id") Long id) {
		Long userId = TokenUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(id, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		if (!templateService.removeTemplateById(id)) {
			return ErrorMessage.Common.SERVER_ERROR;
		}
		templateService.dropTable(template);
		return ResultUtils.success();
	}
	
	@PostMapping("/batch/remove")
	public Result batchRemove(@RequestBody List<Long> ids) {
		if (ids.size() == 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Long userId = TokenUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		List<Template> templates = new ArrayList<>();
		for (Long id : ids) {
			Template template = templateService.getTemplate(id, userId);
			if (templates != null) {
				templates.add(template);
			}
		}
		for (Template template : templates) {
			if (templateService.removeTemplateById(template.getId())) {
				templateService.dropTable(template);
			}
		}
		return ResultUtils.success();
	}
	
	@Override
	protected Header header(Object key) {
		return Header.builder().title(getMessage("template.management")).createUrl("/template/edit").build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.model")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("template.management")).active(true).build());
		return breadcrumbs;
	}
	
	@Override
	protected List<List<Title>> titles(Object key) {
		List<List<Title>> titles = new ArrayList<>();
		List<Title> firstTitle = new ArrayList<>();
		firstTitle.add(Title.builder().fields(Arrays.asList("navigationName")).title(getMessage("navigate")).rowspan(2).build());
		firstTitle.add(Title.builder().fields(Arrays.asList("title")).title(getMessage("title")).rowspan(2).build());
		firstTitle.add(Title.builder().fields(Arrays.asList("columns")).title(getMessage("column.name")).rowspan(2).build());
		firstTitle.add(Title.builder().fields(Arrays.asList("functions")).title(getMessage("functions")).rowspan(2).build());
		firstTitle.add(Title.builder().fields(Arrays.asList("createTime")).title(getMessage("create.time")).rowspan(2).orderBy("").build());
		firstTitle.add(Title.builder().fields(Arrays.asList("updateTime")).title(getMessage("update.time")).rowspan(2).orderBy("").build());
		titles.add(firstTitle);
		return titles;
	}
	
	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("navigationName").title(getMessage("navigate")).view(Views.TEXT.getView()).width(100).build());
		columns.add(Column.builder().field("title").title(getMessage("title")).view(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("columns").title(getMessage("column.name")).view(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("functions").title(getMessage("functions")).view(Views.TEXT.getView()).width(300).build());
		columns.add(Column.builder().field("createTime").title(getMessage("create.time")).view(Views.TEXT.getView()).width(150).orderBy("").build());
		columns.add(Column.builder().field("updateTime").title(getMessage("update.time")).view(Views.TEXT.getView()).width(150).orderBy("").build());
		return columns;
	}
	
	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("navigationId");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("navigate")));
		Map<String, FieldFilter> value = new HashMap<>();
		Map<Long, String> navigationMap = navigationSelection.selectWithParent(null);
		List<Long> navigationValues = new ArrayList<>(navigationMap.size());
		List<String> navigationTexts = new ArrayList<>(navigationMap.size());
		for (Entry<Long, String> entry : navigationMap.entrySet()) {
			navigationValues.add(entry.getKey());
			navigationTexts.add(entry.getValue());
		}
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, navigationValues, navigationTexts));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("title");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, keyValues));
		value = new HashMap<>();
		value.put("title", TextFilter.textFilter("", "text", "strict"));
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
	
	private TemplateInfo buildTemplateInfo(Template template) {
		TemplateInfo templateInfo = new TemplateInfo();
		templateInfo.setId(template.getId());
		templateInfo.setTitle(template.getTitle());
		templateInfo.setColumns(template.getColumns().stream().map(TemplateColumn::getTitle).collect(Collectors.joining(", ")));
		if (template.getFunctions() != null) {
			String[] funcs = template.getFunctions().split(",");
			String functions = Arrays.stream(funcs).map(func -> {
				return getMessage(SystemUtils.humpToCode(func, "."));
			}).collect(Collectors.joining(","));
			templateInfo.setFunctions(functions);
		}
		templateInfo.setCreateTime(template.getCreateTime());
		templateInfo.setUpdateTime(template.getUpdateTime());
		return templateInfo;
	}
	
}
