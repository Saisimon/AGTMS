package net.saisimon.agtms.web.controller.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.SelectFilter;
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
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.MainController;
import net.saisimon.agtms.web.dto.resp.TemplateInfo;
import net.saisimon.agtms.web.selection.NavigationSelection;

/**
 * 模板主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/template/main")
@ControllerInfo("template.management")
public class TemplateMainController extends MainController {
	
	public static final String TEMPLATE = "template";
	private static final String TEMPLATE_FILTERS = TEMPLATE + "_filters";
	private static final String TEMPLATE_PAGEABLE = TEMPLATE + "_pageable";
	private static final List<String> FUNCTIONS = Arrays.asList(
			Functions.CREATE.getFunction(),
			"view",
			Functions.EDIT.getFunction(),
			Functions.REMOVE.getFunction(),
			Functions.BATCH_REMOVE.getFunction()
	);
	private static final Set<String> TEMPLATE_FILTER_FIELDS = new HashSet<>();
	static {
		TEMPLATE_FILTER_FIELDS.add("navigationId");
		TEMPLATE_FILTER_FIELDS.add("title");
		TEMPLATE_FILTER_FIELDS.add("createTime");
		TEMPLATE_FILTER_FIELDS.add("updateTime");
	}
	
	@Autowired
	private NavigationSelection navigationSelection;
	
	@PostMapping("/grid")
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(TEMPLATE));
	}
	
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@RequestParam Map<String, Object> param, @RequestBody Map<String, Object> body) {
		FilterRequest filter = FilterRequest.build(body, TEMPLATE_FILTER_FIELDS);
		filter.and(Constant.OPERATORID, AuthUtils.getUserInfo().getUserId());
		FilterPageable pageable = FilterPageable.build(param);
		TemplateService templateService = TemplateServiceFactory.get();
		Page<Template> page = templateService.findPage(filter, pageable);
		List<TemplateInfo> results = new ArrayList<>(page.getContent().size());
		Map<String, String> navigationMap = navigationSelection.select();
		for (Template template : page.getContent()) {
			TemplateInfo result = buildTemplateInfo(template);
			result.setNavigationName(navigationMap.get(template.getNavigationId().toString()));
			result.setAction(TEMPLATE);
			results.add(result);
		}
		request.getSession().setAttribute(TEMPLATE_FILTERS, body);
		request.getSession().setAttribute(TEMPLATE_PAGEABLE, param);
		return ResultUtils.pageSuccess(results, page.getTotalElements());
	}
	
	@Operate(type=OperateTypes.REMOVE)
	@PostMapping("/remove")
	public Result remove(@RequestParam(name = "id") Long id) {
		Long userId = AuthUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		Template template = templateService.getTemplate(id, userId);
		if (template == null) {
			return ErrorMessage.Template.TEMPLATE_NOT_EXIST;
		}
		templateService.delete(id);
		templateService.dropTable(template);
		return ResultUtils.simpleSuccess();
	}
	
	@Operate(type=OperateTypes.BATCH_REMOVE)
	@PostMapping("/batch/remove")
	public Result batchRemove(@RequestBody List<Long> ids) {
		if (ids.size() == 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Long userId = AuthUtils.getUserInfo().getUserId();
		TemplateService templateService = TemplateServiceFactory.get();
		for (Long id : ids) {
			Template template = templateService.getTemplate(id, userId);
			if (template != null) {
				templateService.delete(id);
				templateService.dropTable(template);
			}
		}
		return ResultUtils.simpleSuccess();
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
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("navigationName").label(getMessage("navigation")).view(Views.TEXT.getView()).width(100).build());
		columns.add(Column.builder().field("title").label(getMessage("title")).view(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("columns").label(getMessage("column.name")).view(Views.TEXT.getView()).width(200).build());
		columns.add(Column.builder().field("functions").label(getMessage("functions")).view(Views.TEXT.getView()).width(300).build());
		columns.add(Column.builder().field("createTime").label(getMessage("create.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").view(Views.TEXT.getView()).width(150).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("updateTime").label(getMessage("update.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").view(Views.TEXT.getView()).width(150).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("action").label(getMessage("actions")).type("number").width(100).build());
		return columns;
	}
	
	@Override
	protected List<Action> actions(Object key) {
		List<Action> actions = new ArrayList<>();
		actions.add(Action.builder().key("view").to("/management/main/").icon("list").text(getMessage("view")).variant("outline-secondary").type("link").build());
		actions.add(Action.builder().key("edit").to("/template/edit?id=").icon("edit").text(getMessage("edit")).type("link").build());
		actions.add(Action.builder().key("remove").to("/template/main/remove").icon("trash").text(getMessage("remove")).variant("outline-danger").type("modal").build());
		return actions;
	}
	
	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("navigationId");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("navigation")));
		Map<String, FieldFilter> value = new HashMap<>();
		Map<Long, String> navigationMap = navigationSelection.selectWithParent(null);
		List<Long> navigationValues = new ArrayList<>(navigationMap.size());
		List<String> navigationTexts = new ArrayList<>(navigationMap.size());
		for (Entry<Long, String> entry : navigationMap.entrySet()) {
			navigationValues.add(entry.getKey());
			navigationTexts.add(entry.getValue());
		}
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getName(), navigationValues, navigationTexts));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("title");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, keyValues));
		value = new HashMap<>();
		value.put("title", TextFilter.textFilter("", Classes.STRING.getName(), SingleSelect.OPERATORS.get(0)));
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
	
	private TemplateInfo buildTemplateInfo(Template template) {
		TemplateInfo templateInfo = new TemplateInfo();
		templateInfo.setId(template.getId());
		templateInfo.setTitle(template.getTitle());
		templateInfo.setColumns(template.getColumns().stream().sorted((c1, c2) -> {
			return c1.getOrdered().compareTo(c2.getOrdered());
		}).map(TemplateColumn::getTitle).collect(Collectors.joining(", ")));
		if (template.getFunction() != null && template.getFunction() != 0) {
			List<String> funcs = TemplateUtils.getFunctions(template);
			String functions = funcs.stream().map(func -> {
				return getMessage(SystemUtils.humpToCode(func, "."));
			}).collect(Collectors.joining(", "));
			templateInfo.setFunctions(functions);
		}
		templateInfo.setCreateTime(template.getCreateTime());
		templateInfo.setUpdateTime(template.getUpdateTime());
		return templateInfo;
	}
	
}
