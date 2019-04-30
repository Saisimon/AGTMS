package net.saisimon.agtms.web.controller.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;
import net.saisimon.agtms.core.domain.filter.FieldFilter;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.domain.filter.RangeFilter;
import net.saisimon.agtms.core.domain.filter.SelectFilter;
import net.saisimon.agtms.core.domain.filter.TextFilter;
import net.saisimon.agtms.core.domain.grid.BatchGrid;
import net.saisimon.agtms.core.domain.grid.BatchGrid.BatchEdit;
import net.saisimon.agtms.core.domain.grid.BatchGrid.BatchExport;
import net.saisimon.agtms.core.domain.grid.BatchGrid.BatchImport;
import net.saisimon.agtms.core.domain.grid.Breadcrumb;
import net.saisimon.agtms.core.domain.grid.Filter;
import net.saisimon.agtms.core.domain.grid.MainGrid;
import net.saisimon.agtms.core.domain.grid.MainGrid.Action;
import net.saisimon.agtms.core.domain.grid.MainGrid.Column;
import net.saisimon.agtms.core.domain.grid.MainGrid.Header;
import net.saisimon.agtms.core.domain.page.Pageable;
import net.saisimon.agtms.core.domain.tag.MultipleSelect;
import net.saisimon.agtms.core.domain.tag.Option;
import net.saisimon.agtms.core.domain.tag.Select;
import net.saisimon.agtms.core.domain.tag.SingleSelect;
import net.saisimon.agtms.web.dto.resp.NavigationTree;
import net.saisimon.agtms.web.dto.resp.NavigationTree.NavigationLink;

/**
 * 主列表页面抽象控制器
 * 
 * @author saisimon
 *
 */
@Slf4j
public abstract class AbstractMainController extends BaseController {
	
	/**
	 * 前端头信息配置
	 * 
	 * @param key 关键词
	 * @return 面包屑导航
	 */
	protected abstract Header header(Object key);
	
	/**
	 * 前端面包屑导航配置
	 * 
	 * @param key 关键词
	 * @return 面包屑导航
	 */
	protected abstract List<Breadcrumb> breadcrumbs(Object key);
	
	/**
	 * 前端过滤条件配置
	 * 
	 * @param key 关键词
	 * @return 过滤条件
	 */
	protected abstract List<Filter> filters(Object key);
	
	/**
	 * 前端列表列信息配置
	 * 
	 * @param key 关键词
	 * @return 列信息
	 */
	protected abstract List<Column> columns(Object key);
	
	/**
	 * 前端操作列信息配置
	 * 
	 * @param key 关键词
	 * @return 操作列信息
	 */
	protected List<Action> actions(Object key) {
		return null;
	}
	
	/**
	 * 前端功能信息配置
	 * 
	 * @param key 关键词
	 * @return 功能信息
	 */
	protected List<String> functions(Object key) {
		return null;
	}
	
	/**
	 * 前端批量编辑配置
	 * 
	 * @param key 关键词
	 * @return 批量编辑信息
	 */
	protected BatchEdit batchEdit(Object key) {
		return null;
	}
	
	/**
	 * 前端批量导出配置
	 * 
	 * @param key 关键词
	 * @return 批量导出信息
	 */
	protected BatchExport batchExport(Object key) {
		return null;
	}
	
	/**
	 * 前端批量导入配置
	 * 
	 * @param key 关键词
	 * @return 批量导入信息
	 */
	protected BatchImport batchImport(Object key) {
		return null;
	}
	
	/**
	 * 前端主要信息的框架
	 * 
	 * @param key 关键词
	 * @return 主要信息
	 */
	protected MainGrid getMainGrid(Object key) {
		if (key == null) {
			return null;
		}
		String sign = sign(key);
		if (sign == null) {
			return null;
		}
		MainGrid mainGrid = new MainGrid();
		mainGrid.setHeader(header(key));
		mainGrid.setBreadcrumbs(breadcrumbs(key));
		List<Column> columns = columns(key);
		previousSort(columns, sign + "_pageable");
		mainGrid.setColumns(columns);
		List<Action> actions = actions(key);
		mainGrid.setActions(actions);
		List<Filter> filters = filters(key);
		boolean showFilters = previousFilter(filters, sign + "_filters");
		mainGrid.setShowFilters(showFilters);
		mainGrid.setFilters(internationFilters(filters));
		Pageable pageable = pageable();
		previousPageable(pageable, sign + "_pageable");
		mainGrid.setPageable(pageable);
		mainGrid.setFunctions(functions(key));
		return mainGrid;
	}
	
	protected String sign(Object key) {
		if (key == null) {
			return null;
		}
		return key.toString();
	}
	
	/**
	 * 前端批量处理相关信息的框架
	 * 
	 * @param key 关键词
	 * @return 批量处理相关信息
	 */
	protected BatchGrid getBatchGrid(Object key) {
		if (key == null) {
			return null;
		}
		BatchGrid batchGrid = new BatchGrid();
		BatchEdit batchEdit = batchEdit(key);
		batchGrid.setBatchEdit(batchEdit);
		BatchExport batchExport = batchExport(key);
		batchGrid.setBatchExport(batchExport);
		BatchImport batchImport = batchImport(key);
		batchGrid.setBatchImport(batchImport);
		return batchGrid;
	}
	
	protected Pageable pageable() {
		return Pageable.builder().pageIndex(1).pageSize(10).build();
	}
	
	protected List<NavigationTree> internationNavigationTrees(List<NavigationTree> trees) {
		if (CollectionUtils.isEmpty(trees)) {
			return null;
		}
		List<NavigationTree> internationNavigationTrees = new ArrayList<>();
		for (NavigationTree tree : trees) {
			try {
				NavigationTree cloneTree = (NavigationTree) tree.clone();
				cloneTree.setTitle(getMessage(cloneTree.getTitle()));
				if (!CollectionUtils.isEmpty(cloneTree.getLinks())) {
					for (NavigationLink link : cloneTree.getLinks()) {
						link.setName(getMessage(link.getName()));
					}
				}
				cloneTree.setChildrens(internationNavigationTrees(cloneTree.getChildrens()));
				internationNavigationTrees.add(cloneTree);
			} catch (CloneNotSupportedException e) {
				log.error("clone navigation tree failed", e);
				internationNavigationTrees.add(tree);
			}
		}
		return internationNavigationTrees;
	}
	
	private List<Filter> internationFilters(List<Filter> filters) {
		if (CollectionUtils.isEmpty(filters)) {
			return null;
		}
		List<Filter> internationFilters = new ArrayList<>();
		for (Filter filter : filters) {
			try {
				Filter cloneFilter = (Filter) filter.clone();
				internationFilterSelect(cloneFilter.getKey());
				Map<String, FieldFilter> value = cloneFilter.getValue();
				for (FieldFilter fieldFilter : value.values()) {
					if (fieldFilter instanceof TextFilter) {
						TextFilter<?> textFilter = (TextFilter<?>)fieldFilter;
						internationFilterSelect(textFilter.getOperator());
					}
				}
				internationFilters.add(cloneFilter);
			} catch (CloneNotSupportedException e) {
				log.error("clone filter failed", e);
				internationFilters.add(filter);
			}
		}
		return internationFilters;
	}
	
	private <T> void internationFilterSelect(Select<T> filterSelect) {
		if (filterSelect instanceof SingleSelect) {
			SingleSelect<T> filterSingleSelect = (SingleSelect<T>) filterSelect;
			Option<T> selected = filterSingleSelect.getSelected();
			selected.setText(getMessage(selected.getText()));
		} else if (filterSelect instanceof MultipleSelect) {
			MultipleSelect<T> filterMultipleSelect = (MultipleSelect<T>) filterSelect;
			List<Option<T>> selected = filterMultipleSelect.getSelected();
			for (Option<T> selectedOption : selected) {
				selectedOption.setText(getMessage(selectedOption.getText()));
			}
		}
		List<Option<T>> options = filterSelect.getOptions();
		for (Option<T> option : options) {
			option.setText(getMessage(option.getText()));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void previousPageable(Pageable pageable, String sessionKey) {
		if (pageable == null) {
			return;
		}
		HttpSession session = request.getSession();
		Map<String, String> pageableMap = (Map<String, String>) session.getAttribute(sessionKey);
		if (pageableMap == null) {
			return;
		}
		String index = pageableMap.get(Constant.Param.INDEX);
		if (index != null) {
			pageable.setPageIndex(Integer.parseInt(index) + 1);
		}
		String size = pageableMap.get(Constant.Param.SIZE);
		if (size != null) {
			pageable.setPageSize(Integer.parseInt(size));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void previousSort(List<Column> columns, String sessionKey) {
		if (CollectionUtils.isEmpty(columns)) {
			return;
		}
		HttpSession session = request.getSession();
		Map<String, String> pageableMap = (Map<String, String>) session.getAttribute(sessionKey);
		if (pageableMap == null) {
			return;
		}
		String sort = pageableMap.get(Constant.Param.SORT);
		Map<String, String> sortMap = FilterSort.build(sort).getSortMap();
		for (Column column : columns) {
			if (column.getField() != null) {
				column.setOrderBy(sortMap.get(column.getField()));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean previousFilter(List<Filter> filters, String sessionKey) {
		if (CollectionUtils.isEmpty(filters)) {
			return false;
		}
		HttpSession session = request.getSession();
		Map<String, Object> filterMap = (Map<String, Object>) session.getAttribute(sessionKey);
		if (filterMap == null) {
			return false;
		}
		List<Map<String, Object>> andFilters = (List<Map<String, Object>>) filterMap.get("andFilters");
		if (CollectionUtils.isEmpty(andFilters)) {
			return false;
		}
		for (Map<String, Object> andFilter : andFilters) {
			String key = (String) andFilter.get("key");
			String operator = (String) andFilter.get("operator");
			Object value = andFilter.get("value");
			if (value == null) {
				continue;
			}
			for (Filter filter : filters) {
				SingleSelect<String> keySelect = filter.getKey();
				for (Option<String> option : keySelect.getOptions()) {
					if (option.getValue().equals(key)) {
						keySelect.setSelected(option);
						break;
					}
				}
				for (Entry<String, FieldFilter> entry : filter.getValue().entrySet()) {
					if (!entry.getKey().equals(key)) {
						continue;
					}
					FieldFilter fieldFilter = entry.getValue();
					if ("text".equals(fieldFilter.getType())) {
						TextFilter<Object> textFilter = (TextFilter<Object>) fieldFilter;
						textFilter.getInput().setValue(value);
						if (Operator.EQ.equals(operator)) {
							textFilter.getOperator().setSelected(Option.STRICT);
						} else if (Operator.REGEX.equals(operator)) {
							textFilter.getOperator().setSelected(Option.FUZZY);
						} else if (Operator.IN.equals(operator)) {
							textFilter.getOperator().setSelected(Option.SEPARATOR);
						}
					} else if ("select".equals(fieldFilter.getType())) {
						SelectFilter<Object> selectFilter = (SelectFilter<Object>) fieldFilter;
						if (selectFilter.isMultiple()) {
							MultipleSelect<Object> filterMultipleSelect = (MultipleSelect<Object>) selectFilter.getSelect();
							List<Object> list = (List<Object>) value;
							List<Option<Object>> selected = new ArrayList<>();
							for (Option<Object> option : filterMultipleSelect.getOptions()) {
								for (Object obj : list) {
									if (option.getValue().toString().equals(obj.toString())) {
										selected.add(option);
									}
								}
							}
							filterMultipleSelect.setSelected(selected);
						} else {
							SingleSelect<Object> filterSingleSelect = (SingleSelect<Object>) selectFilter.getSelect();
							for (Option<Object> option : filterSingleSelect.getOptions()) {
								if (option.getValue().toString().equals(value.toString())) {
									filterSingleSelect.setSelected(option);
									break;
								}
							}
						}
					} else if ("range".equals(fieldFilter.getType())) {
						RangeFilter<Object> rangeFilter = (RangeFilter<Object>) fieldFilter;
						if (Operator.LTE.equals(operator)) {
							rangeFilter.getTo().setValue(value);
						} else if (Operator.GTE.equals(operator)) {
							rangeFilter.getFrom().setValue(value);
						}
					}
				}
			}
		}
		return true;
	}
	
}
