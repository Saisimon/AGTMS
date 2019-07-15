package net.saisimon.agtms.core.domain.filter;

import static net.saisimon.agtms.core.constant.Constant.Param.INDEX;
import static net.saisimon.agtms.core.constant.Constant.Param.PARAM;
import static net.saisimon.agtms.core.constant.Constant.Param.SIZE;
import static net.saisimon.agtms.core.constant.Constant.Param.SORT;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import cn.hutool.core.map.MapUtil;
import lombok.Getter;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.constant.Constant.Operator;

/**
 * 分页过滤对象
 * 
 * @author saisimon
 *
 */
@Getter
public class FilterPageable implements Serializable {
	
	private static final long serialVersionUID = 8260532524227922998L;
	
	private static final int DEFAULT_INDEX = 0;
	private static final int DEFAULT_SIZE = 10;
	private static final int DEFAULT_MAX_SIZE = 100;
	
	private FilterParam param;
	private int index = DEFAULT_INDEX;
	private int size = DEFAULT_SIZE;
	private FilterSort sort = new FilterSort();
	
	public FilterPageable(int index, int size, FilterSort sort) {
		this(null, index, size, sort);
	}

	public FilterPageable(FilterParam param, int size, FilterSort sort) {
		this(param, 0, size, sort);
	}

	private FilterPageable(FilterParam param, int index, int size, FilterSort sort) {
		if (size < 1) {
			throw new IllegalArgumentException("page size must not be less than one!");
		}
		this.size = size;
		if (index > 0) {
			this.index = index;
		}
		if (param != null) {
			this.param = param;
		}
		if (sort != null) {
			this.sort = sort;
		} else {
			this.sort = FilterSort.build(Constant.ID, Direction.DESC);
		}
	}
	
	public FilterPageable asc(String key) {
		sort.asc(key);
		return this;
	}
	
	public FilterPageable desc(String key) {
		sort.desc(key);
		return this;
	}
	
	public Map<String, String> getSortMap() {
		return sort.getSortMap();
	}
	
	public Pageable getPageable() {
		if (param == null) {
			return PageRequest.of(index, size, sort.getSort());
		} else {
			return PageRequest.of(0, size, sort.getSort());
		}
	}
	
	@SuppressWarnings("unchecked")
	public static FilterPageable build(Map<String, Object> pageableMap) {
		int index = DEFAULT_INDEX;
		int size = DEFAULT_SIZE;
		FilterSort sort = null;
		FilterParam param = null;
		if (pageableMap != null) {
			Object paramObj = pageableMap.get(PARAM);
			if (null != paramObj && paramObj instanceof Map) {
				param = FilterParam.build((Map<String, Object>)paramObj);
				if (Operator.GT.equals(param.getOperator())) {
					sort = FilterSort.build("id", Direction.ASC);
				} else if (Operator.LT.equals(param.getOperator())) {
					sort = FilterSort.build("id", Direction.DESC);
				}
			} else {
				Object sortObj = pageableMap.get(SORT);
				if (null != sortObj) {
					sort = FilterSort.build(sortObj.toString());
				}
			}
			Object indexObj = pageableMap.get(INDEX);
			if (null != indexObj) {
				try {
					index = Integer.parseInt(indexObj.toString());
				} catch (NumberFormatException e) {
					index = DEFAULT_INDEX;
				}
			}
			Object sizeObj = pageableMap.get(SIZE);
			if (null != sizeObj) {
				try {
					size = Integer.parseInt(sizeObj.toString());
				} catch (NumberFormatException e) {
					size = DEFAULT_SIZE;
				}
				if (size > DEFAULT_MAX_SIZE) {
					size = DEFAULT_MAX_SIZE;
				}
			}
		}
		return new FilterPageable(param, index, size, sort);
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> pageableMap = MapUtil.newHashMap(3);
		pageableMap.put(INDEX, index);
		pageableMap.put(PARAM, param);
		pageableMap.put(SIZE, size);
		pageableMap.put(SORT, sort.toString());
		return pageableMap;
	}
	
}
