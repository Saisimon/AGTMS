package net.saisimon.agtms.web.controller.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.saisimon.agtms.core.annotation.ControllerInfo;
import net.saisimon.agtms.core.annotation.Operate;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Selection;
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
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.service.SelectionService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.controller.base.MainController;
import net.saisimon.agtms.web.dto.resp.SelectionInfo;

/**
 * 下拉列表主控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/selection/main")
@ControllerInfo("selection.management")
public class SelectionMainController extends MainController {
	
	public static final String SELECTION = "selection";
	private static final String SELECTION_FILTERS = SELECTION + "_filters";
	private static final String SELECTION_PAGEABLE = SELECTION + "_pageable";
	private static final List<String> FUNCTIONS = Arrays.asList(
			Functions.CREATE.getFunction(),
			Functions.EDIT.getFunction(),
			Functions.REMOVE.getFunction(),
			Functions.BATCH_REMOVE.getFunction()
	);
	private static final Set<String> SELECTION_FILTER_FIELDS = new HashSet<>();
	static {
		SELECTION_FILTER_FIELDS.add("title");
		SELECTION_FILTER_FIELDS.add("createTime");
		SELECTION_FILTER_FIELDS.add("updateTime");
	}
	
	@PostMapping("/grid")
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(SELECTION));
	}
	
	@Operate(type=OperateTypes.QUERY, value="list")
	@PostMapping("/list")
	public Result list(@RequestParam Map<String, Object> param, @RequestBody Map<String, Object> body) {
		FilterRequest filter = FilterRequest.build(body, SELECTION_FILTER_FIELDS);
		filter.and(Constant.OPERATORID, AuthUtils.getUserInfo().getUserId());
		FilterPageable pageable = FilterPageable.build(param);
		SelectionService selectionService = SelectionServiceFactory.get();
		Page<Selection> page = selectionService.findPage(filter, pageable);
		List<SelectionInfo> results = new ArrayList<>(page.getContent().size());
		for (Selection selection : page.getContent()) {
			SelectionInfo result = new SelectionInfo();
			result.setId(selection.getId());
			result.setCreateTime(selection.getCreateTime());
			result.setTitle(selection.getTitle());
			result.setUpdateTime(selection.getUpdateTime());
			result.setAction(SELECTION);
			results.add(result);
		}
		request.getSession().setAttribute(SELECTION_FILTERS, body);
		request.getSession().setAttribute(SELECTION_PAGEABLE, param);
		return ResultUtils.pageSuccess(results, page.getTotalElements());
	}
	
	@Operate(type=OperateTypes.REMOVE)
	@Transactional
	@PostMapping("/remove")
	public Result remove(@RequestParam(name = "id") Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Long userId = AuthUtils.getUserInfo().getUserId();
		SelectionService selectionService = SelectionServiceFactory.get();
		Selection selection = selectionService.getSelection(id, userId);
		if (selection == null) {
			return ErrorMessage.Selection.SELECTION_NOT_EXIST;
		}
		if (SelectTypes.OPTION.getType().equals(selection.getType())) {
			selectionService.removeSelectionOptions(selection.getId());
		} else if (SelectTypes.TEMPLATE.getType().equals(selection.getType())) {
			selectionService.removeSelectionTemplate(selection.getId());
		}
		selectionService.delete(selection);
		return ResultUtils.simpleSuccess();
	}
	
	@Operate(type=OperateTypes.BATCH_REMOVE)
	@Transactional
	@PostMapping("/batch/remove")
	public Result batchRemove(@RequestBody List<Long> ids) {
		if (ids.size() == 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Long userId = AuthUtils.getUserInfo().getUserId();
		SelectionService selectionService = SelectionServiceFactory.get();
		for (Long id : ids) {
			Selection selection = selectionService.getSelection(id, userId);
			if (selection == null) {
				continue;
			}
			if (SelectTypes.OPTION.getType().equals(selection.getType())) {
				selectionService.removeSelectionOptions(selection.getId());
			} else if (SelectTypes.TEMPLATE.getType().equals(selection.getType())) {
				selectionService.removeSelectionTemplate(selection.getId());
			}
			selectionService.delete(selection);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected Header header(Object key) {
		return Header.builder().title(getMessage("selection.management")).createUrl("/selection/edit").build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("system.model")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(getMessage("selection.management")).active(true).build());
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("title");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, keyValues));
		Map<String, FieldFilter> value = new HashMap<>();
		value.put(keyValues.get(0), TextFilter.textFilter("", Classes.STRING.getName(), SingleSelect.OPERATORS.get(0)));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("createTime", "updateTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("create.time", "update.time")));
		value = new HashMap<>();
		value.put(keyValues.get(0), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}

	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("title").label(getMessage("title")).width(200).view(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("createTime").label(getMessage("create.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).view(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("updateTime").label(getMessage("update.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).view(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("action").label(getMessage("actions")).type("number").width(100).build());
		return columns;
	}
	
	@Override
	protected List<Action> actions(Object key) {
		List<Action> actions = new ArrayList<>();
		actions.add(Action.builder().key("edit").to("/selection/edit?id=").icon("edit").text(getMessage("edit")).type("link").build());
		actions.add(Action.builder().key("remove").icon("trash").to("/selection/main/remove").text(getMessage("remove")).variant("outline-danger").type("modal").build());
		return actions;
	}
	
	@Override
	protected List<String> functions(Object key) {
		return FUNCTIONS;
	}

}