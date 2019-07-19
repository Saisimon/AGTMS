package net.saisimon.agtms.core.domain.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import lombok.Getter;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 排序过滤对象
 * 
 * @author saisimon
 *
 */
@Getter
public class FilterSort implements Serializable {
	
	private static final long serialVersionUID = 3863739605298788499L;
	
	private Map<String, String> sortMap = new LinkedHashMap<>();
	
	public static FilterSort build(String key, Direction dirc) {
		FilterSort sort = new FilterSort();
		sort.sort(key, dirc);
		return sort;
	}
	
	public FilterSort asc(String key) {
		return sort(key, Direction.ASC);
	}
	
	public FilterSort desc(String key) {
		return sort(key, Direction.DESC);
	}
	
	public FilterSort sort(String key, Direction dirc) {
		sortMap.put(key, dirc.name().toLowerCase());
		return this;
	}
	
	public Sort getSort() {
		List<Order> orders = new ArrayList<>();
		for (Entry<String, String> entry : sortMap.entrySet()) {
			if (Direction.DESC.name().equalsIgnoreCase(entry.getValue())) {
				orders.add(Order.desc(entry.getKey()));
			} else {
				orders.add(Order.asc(entry.getKey()));
			}
		}
		return Sort.by(orders);
	}
	
	public static FilterSort build(String sort) {
		String str = sort;
		if (SystemUtils.isBlank(str)) {
			str = "-id";
		}
		str = str.trim();
		FilterSort filterSort = new FilterSort();
		String[] ss = str.split(",");
		if (ss != null) {
			for (String key : ss) {
				if (key.length() == 0) {
					continue;
				}
				Direction direction = Sort.DEFAULT_DIRECTION;
				char c = key.charAt(0);
				if ('+' == c) {
					direction = Direction.ASC;
					key = key.substring(1);
				} else if ('-' == c) {
					direction = Direction.DESC;
					key = key.substring(1);
				}
				filterSort.sort(key, direction);
			}
		}
		return filterSort;
	}
	
	@Override
	public String toString() {
		List<String> orders = new ArrayList<>();
		for (Entry<String, String> entry : sortMap.entrySet()) {
			if (Direction.DESC.name().equalsIgnoreCase(entry.getValue())) {
				orders.add("-" + entry.getKey());
			} else {
				orders.add("+" + entry.getKey());
			}
		}
		return orders.stream().collect(Collectors.joining(","));
	}
	
}
