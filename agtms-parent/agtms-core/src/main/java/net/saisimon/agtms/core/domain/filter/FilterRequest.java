package net.saisimon.agtms.core.domain.filter;

import static net.saisimon.agtms.core.constant.Constant.Operator.AND;
import static net.saisimon.agtms.core.constant.Constant.Operator.EQ;
import static net.saisimon.agtms.core.constant.Constant.Operator.OR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import lombok.Getter;
import lombok.Setter;

/**
 * 条件过滤对象
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
public class FilterRequest implements Serializable {
	
	private static final long serialVersionUID = 9055844361079917020L;
	
	private List<FilterRequest> andFilters;
	private List<FilterRequest> orFilters;
	
	public FilterRequest clear() {
		if (andFilters != null) {
			andFilters.clear();
		}
		if (orFilters != null) {
			orFilters.clear();
		}
		return this;
	}
	
	public FilterRequest and(FilterRequest filter) {
		return add(AND, filter);
	}
	
	public FilterRequest and(String key, Object value) {
		return and(key, value, EQ);
	}
	
	public FilterRequest and(String key, Object value, String operator) {
		return add(AND, key, value, operator);
	}
	
	public FilterRequest or(FilterRequest filter) {
		return add(OR, filter);
	}
	
	public FilterRequest or(String key, Object value) {
		return or(key, value, EQ);
	}
	
	public FilterRequest or(String key, Object value, String operator) {
		return add(OR, key, value, operator);
	}
	
	private FilterRequest add(String joiner, String key, Object value, String operator) {
		FilterParam param = FilterParam.build(key, value, operator);
		return add(joiner, param);
	}
	
	private FilterRequest add(String joiner, FilterRequest filter) {
		if (filter != null) {
			if (OR.equals(joiner)) {
				if (orFilters == null) {
					orFilters = new ArrayList<>();
				}
				orFilters.add(filter);
			} else {
				if (andFilters == null) {
					andFilters = new ArrayList<>();
				}
				andFilters.add(filter);
			}
		}
		return this;
	}
	
	public static FilterRequest build() {
		return new FilterRequest();
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> filterMap = new HashMap<>();
		if (andFilters != null) {
			List<Map<String, Object>> andFilterList = new ArrayList<>();
			for (FilterRequest filterRequest : andFilters) {
				if (filterRequest instanceof FilterParam) {
					andFilterList.add(((FilterParam) filterRequest).toMap());
				} else {
					andFilterList.add(filterRequest.toMap());
				}
			}
			filterMap.put("andFilters", andFilterList);
		}
		if (orFilters != null) {
			List<Map<String, Object>> orFilterList = new ArrayList<>();
			for (FilterRequest filterRequest : orFilters) {
				if (filterRequest instanceof FilterParam) {
					orFilterList.add(((FilterParam) filterRequest).toMap());
				} else {
					orFilterList.add(filterRequest.toMap());
				}
			}
			filterMap.put("orFilters", orFilterList);
		}
		return filterMap;
	}
	
	public static FilterRequest build(Map<String, Object> map, Set<String> filterFields) {
		if (!CollectionUtils.isEmpty(map)) {
			FilterRequest filterRequest = build();
			filterRequest.setAndFilters(parseMap(map, "andFilters", filterFields));
			filterRequest.setOrFilters(parseMap(map, "orFilters", filterFields));
			return filterRequest;
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static List<FilterRequest> parseMap(Map<String, Object> map, String key, Set<String> filterFields) {
		List<Map<String, Object>> filterMaps = (List<Map<String, Object>>) map.get(key);
		if (filterMaps != null) {
			List<FilterRequest> filters = new ArrayList<>(filterMaps.size());
			for (Map<String, Object> filterMap : filterMaps) {
				if (filterMap.get("andFilters") != null || filterMap.get("orFilters") != null) {
					FilterRequest request = FilterRequest.build(filterMap, filterFields);
					if (request != null) {
						filters.add(request);
					}
				} else {
					FilterParam param = FilterParam.build(filterMap, filterFields);
					if (param != null) {
						filters.add(param);
					}
				}
			}
			return filters;
		} else {
			return null;
		}
	}
	
}