package net.saisimon.agtms.core.domain.filter;

import static net.saisimon.agtms.core.constant.Constant.Operator.EQ;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.util.StringUtils;

@Setter
@Getter
@Slf4j
public class FilterParam extends FilterRequest {
	
	private static final long serialVersionUID = -4082925119637610947L;
	
	private String key;
	private String type = "java.lang.String";
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
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key can not be blank");
		}
		if (value == null) {
			throw new IllegalArgumentException("value can not be null");
		}
		this.key = key;
		this.operator = operator;
		this.value = value;
		if (StringUtils.isBlank(type)) {
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
	
}
