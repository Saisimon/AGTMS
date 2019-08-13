package net.saisimon.agtms.core.domain.filter;

import static net.saisimon.agtms.core.constant.Constant.Operator.EQ;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;

import cn.hutool.core.map.MapUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 条件过滤对象
 * 
 * @author saisimon
 *
 */
@Setter
@Getter
@Slf4j
public class FilterParam extends FilterRequest {
	
	private static final long serialVersionUID = -4082925119637610947L;
	
	private String key;
	private String type = Classes.STRING.getName();
	private String operator;
	private Object value;
	
	private FilterParam() {
		
	}
	
	public FilterParam(String key, Object value) {
		this(key, value, EQ);
	}
	
	public FilterParam(String key, Object value, String operator) {
		this(key, value, operator, null);
	}
	
	public FilterParam(String key, Object value, String operator, String type) {
		if (SystemUtils.isBlank(key)) {
			throw new IllegalArgumentException("key can not be blank");
		}
		this.key = key;
		this.operator = operator;
		this.value = value;
		if (SystemUtils.isBlank(type) && value != null) {
			this.type = value.getClass().getName();
		} else {
			this.type = type;
		}
	}
	
	public static FilterParam build(String key, Object value, String operator) {
		return new FilterParam(key, value, operator);
	}
	
	public static FilterParam build(Map<String, Object> map) {
		try {
			FilterParam filterParam = new FilterParam();
			BeanUtils.populate(filterParam, map);
			return filterParam;
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("build filter param error", e);
			return null;
		}
	}
	
	public static FilterParam build(Map<String, Object> map, Set<String> filterFields) {
		try {
			Object key = map.get("key");
			if (key == null) {
				return null;
			}
			if (filterFields != null && !filterFields.contains(key.toString())) {
				return null;
			}
			FilterParam filterParam = new FilterParam();
			BeanUtils.populate(filterParam, map);
			return filterParam;
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("build filter param error", e);
			return null;
		}
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> filterMap = MapUtil.newHashMap(4);
		filterMap.put("key", key);
		filterMap.put("type", type);
		filterMap.put("operator", operator);
		filterMap.put("value", value);
		return filterMap;
	}

	@Override
	public FilterParam clone() {
		FilterParam filterParam = new FilterParam();
		filterParam.setKey(key);
		filterParam.setOperator(operator);
		filterParam.setType(type);
		filterParam.setValue(value);
		return filterParam;
	}
	
}
