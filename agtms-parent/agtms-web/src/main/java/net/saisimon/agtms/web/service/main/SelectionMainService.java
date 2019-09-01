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
import java.util.Map.Entry;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Selection;
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
import net.saisimon.agtms.core.enums.SelectTypes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.SelectionServiceFactory;
import net.saisimon.agtms.core.service.SelectionService;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;
import net.saisimon.agtms.web.dto.resp.SelectionInfo;
import net.saisimon.agtms.web.selection.SelectTypeSelection;
import net.saisimon.agtms.web.selection.UserSelection;
import net.saisimon.agtms.web.service.base.AbstractMainService;
import net.saisimon.agtms.web.service.common.PremissionService;

/**
 * 下拉列表主服务
 * 
 * @author saisimon
 *
 */
@Service
public class SelectionMainService extends AbstractMainService {
	
	public static final String SELECTION = "selection";
	public static final List<Functions> SUPPORT_FUNCTIONS = Arrays.asList(
			Functions.VIEW,
			Functions.CREATE, 
			Functions.EDIT,
			Functions.REMOVE,
			Functions.BATCH_REMOVE
	);
	
	private static final String SELECTION_FILTERS = SELECTION + FILTER_SUFFIX;
	private static final String SELECTION_PAGEABLE = SELECTION + PAGEABLE_SUFFIX;
	private static final Set<String> SELECTION_FILTER_FIELDS = new HashSet<>();
	static {
		SELECTION_FILTER_FIELDS.add("title");
		SELECTION_FILTER_FIELDS.add("type");
		SELECTION_FILTER_FIELDS.add("createTime");
		SELECTION_FILTER_FIELDS.add("updateTime");
		SELECTION_FILTER_FIELDS.add(Constant.OPERATORID);
	}
	
	@Autowired
	private SelectTypeSelection selectTypeSelection;
	@Autowired
	private UserSelection userSelection;
	@Autowired
	private PremissionService premissionService;
	
	public Result grid() {
		return ResultUtils.simpleSuccess(getMainGrid(SELECTION));
	}
	
	public Result list(Map<String, Object> body) {
		Map<String, Object> filterMap = get(body, FILTER);
		Map<String, Object> pageableMap = get(body, PAGEABLE);
		FilterRequest filter = FilterRequest.build(filterMap, SELECTION_FILTER_FIELDS);
		if (filter == null) {
			filter = FilterRequest.build();
		}
		filter.and(Constant.OPERATORID, premissionService.getUserIds(AuthUtils.getUid()), Constant.Operator.IN);
		if (pageableMap != null) {
			pageableMap.remove(PARAM);
		}
		FilterPageable pageable = FilterPageable.build(pageableMap);
		SelectionService selectionService = SelectionServiceFactory.get();
		List<Selection> list = selectionService.findPage(filter, pageable, false).getContent();
		List<SelectionInfo> results = new ArrayList<>(list.size());
		Map<Integer, String> selectTypeMap = selectTypeSelection.select();
		Map<String, String> userMap = userSelection.select();
		for (Selection selection : list) {
			SelectionInfo result = new SelectionInfo();
			result.setId(selection.getId().toString());
			result.setCreateTime(selection.getCreateTime());
			result.setTitle(selection.getTitle());
			result.setType(selectTypeMap.get(selection.getType()));
			result.setUpdateTime(selection.getUpdateTime());
			result.setOperator(userMap.get(selection.getOperatorId().toString()));
			result.setAction(SELECTION);
			results.add(result);
		}
		request.getSession().setAttribute(SELECTION_FILTERS, filterMap);
		request.getSession().setAttribute(SELECTION_PAGEABLE, pageableMap);
		return ResultUtils.pageSuccess(results, list.size() < pageable.getSize());
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result remove(Long id) {
		if (id < 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Selection selection = SelectionUtils.getSelection(id, premissionService.getUserIds(AuthUtils.getUid()));
		if (selection == null) {
			return ErrorMessage.Selection.SELECTION_NOT_EXIST;
		}
		SelectionService selectionService = SelectionServiceFactory.get();
		if (SelectTypes.OPTION.getType().equals(selection.getType())) {
			selectionService.removeSelectionOptions(selection);
		} else if (SelectTypes.TEMPLATE.getType().equals(selection.getType())) {
			selectionService.removeSelectionTemplate(selection);
		}
		selectionService.delete(selection);
		return ResultUtils.simpleSuccess();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Result batchRemove(List<Long> ids) {
		if (ids.size() == 0) {
			return ErrorMessage.Common.MISSING_REQUIRED_FIELD;
		}
		Set<Long> userIds = premissionService.getUserIds(AuthUtils.getUid());
		SelectionService selectionService = SelectionServiceFactory.get();
		for (Long id : ids) {
			Selection selection = SelectionUtils.getSelection(id, userIds);
			if (selection == null) {
				continue;
			}
			if (SelectTypes.OPTION.getType().equals(selection.getType())) {
				selectionService.removeSelectionOptions(selection);
			} else if (SelectTypes.TEMPLATE.getType().equals(selection.getType())) {
				selectionService.removeSelectionTemplate(selection);
			}
			selectionService.delete(selection);
		}
		return ResultUtils.simpleSuccess();
	}
	
	@Override
	protected Header header(Object key) {
		return Header.builder().title(messageService.getMessage("selection.management")).createUrl("/selection/edit").build();
	}

	@Override
	protected List<Breadcrumb> breadcrumbs(Object key) {
		List<Breadcrumb> breadcrumbs = new ArrayList<>();
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("system.module")).to("/").build());
		breadcrumbs.add(Breadcrumb.builder().text(messageService.getMessage("selection.management")).active(true).build());
		return breadcrumbs;
	}

	@Override
	protected List<Filter> filters(Object key) {
		List<Filter> filters = new ArrayList<>();
		Filter filter = new Filter();
		List<String> keyValues = Arrays.asList("type");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("type")));
		Map<String, FieldFilter> value = new HashMap<>(4);
		Map<Integer, String> selectTypeMap = selectTypeSelection.select();
		List<Integer> selectTypeValues = new ArrayList<>(selectTypeMap.size());
		List<String> selectTypeTexts = new ArrayList<>(selectTypeMap.size());
		for (Entry<Integer, String> entry : selectTypeMap.entrySet()) {
			selectTypeValues.add(entry.getKey());
			selectTypeTexts.add(entry.getValue());
		}
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getName(), selectTypeValues, selectTypeTexts));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("title");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, keyValues));
		value = new HashMap<>(4);
		value.put(keyValues.get(0), TextFilter.textFilter("", Classes.STRING.getName(), SingleSelect.OPERATORS.get(0)));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("createTime", "updateTime");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("create.time", "update.time")));
		value = new HashMap<>(4);
		value.put(keyValues.get(0), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		value.put(keyValues.get(1), RangeFilter.rangeFilter("", Classes.DATE.getName(), "", Classes.DATE.getName()));
		filter.setValue(value);
		filters.add(filter);
		
		filter = new Filter();
		keyValues = Arrays.asList("operatorId");
		filter.setKey(SingleSelect.select(keyValues.get(0), keyValues, Arrays.asList("operator")));
		value = new HashMap<>(4);
		Map<String, String> userMap = userSelection.select();
		List<String> userValues = new ArrayList<>(userMap.size());
		List<String> userTexts = new ArrayList<>(userMap.size());
		for (Entry<String, String> entry : userMap.entrySet()) {
			userValues.add(entry.getKey());
			userTexts.add(entry.getValue());
		}
		value.put(keyValues.get(0), SelectFilter.selectFilter(null, Classes.LONG.getName(), userValues, userTexts));
		filter.setValue(value);
		filters.add(filter);
		return filters;
	}

	@Override
	protected List<Column> columns(Object key) {
		List<Column> columns = new ArrayList<>();
		columns.add(Column.builder().field("title").label(messageService.getMessage("title")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("type").label(messageService.getMessage("type")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("createTime").label(messageService.getMessage("create.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("updateTime").label(messageService.getMessage("update.time")).type("date").dateInputFormat("YYYY-MM-DDTHH:mm:ss.SSSZZ").dateOutputFormat("YYYY-MM-DD HH:mm:ss").width(400).views(Views.TEXT.getView()).sortable(true).orderBy("").build());
		columns.add(Column.builder().field("operator").label(messageService.getMessage("operator")).width(200).views(Views.TEXT.getView()).build());
		columns.add(Column.builder().field("action").label(messageService.getMessage("actions")).type("number").width(100).build());
		return columns;
	}
	
	@Override
	protected List<Action> actions(Object key) {
		List<Action> actions = new ArrayList<>();
		actions.add(Action.builder().key("edit").to("/selection/edit?id=").icon("edit").text(messageService.getMessage("edit")).type("link").build());
		actions.add(Action.builder().key("remove").icon("trash").to("/selection/main/remove").text(messageService.getMessage("remove")).variant("outline-danger").type("modal").build());
		return actions;
	}
	
	@Override
	protected List<Functions> functions(Object key) {
		return SUPPORT_FUNCTIONS;
	}

}
