package net.saisimon.agtms.core.util;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.util.StringUtils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.exception.AgtmsException;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DomainUtils {
	
	/**
	 * set 前缀
	 */
	private static final String SETTER_PREFIX = "set";
	/**
	 * get 前缀
	 */
	private static final String GETTER_PREFIX = "get";
	
	/**
	 * 获取属性值
	 * 
	 * @param domain 自定义对象
	 * @param fieldName 属性名称
	 * @return 属性值
	 */
	public static Object getField(Domain domain, String fieldName) {
		if (domain == null) {
			throw new AgtmsException("domain can not be null");
		}
		try {
			String getterMethodName = GETTER_PREFIX + SystemUtils.capitalize(fieldName);
			Method m = domain.getClass().getMethod(getterMethodName, new Class<?>[]{});
			return m.invoke(domain);
		} catch (Exception e) {
			log.error(domain.getClass().getName() + " get field("+ fieldName +") error, msg : " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * 设置属性值
	 * 
	 * @param domain 自定义对象
	 * @param fieldName 属性名称
	 * @param fieldValue 属性值
	 * @param fieldClass 属性类型
	 */
	public static void setField(Domain domain, String fieldName, Object fieldValue, Class<?> fieldClass) {
		if (domain == null) {
			throw new AgtmsException("domain can not be null");
		}
		try {
			String setterMethodName = SETTER_PREFIX + SystemUtils.capitalize(fieldName);
			Method m = domain.getClass().getMethod(setterMethodName, fieldClass);
			m.invoke(domain, fieldValue);
		} catch (Exception e) {
			log.error(domain.getClass().getName() + " set field("+ fieldName +") error, msg : " + e.getMessage());
		}
	}
	
	public static void fillCommonFields(Domain newDomain, Domain oldDomain, Long operatorId) {
		if (newDomain == null) {
			return;
		}
		Date time = new Date();
		if (oldDomain == null) {
			newDomain.setField(Constant.CREATETIME, time, Date.class);
			newDomain.setField(Constant.OPERATORID, operatorId, Long.class);
		} else {
			newDomain.setField(Constant.ID, oldDomain.getField(Constant.ID), Long.class);
			newDomain.setField(Constant.CREATETIME, oldDomain.getField(Constant.CREATETIME), Date.class);
			newDomain.setField(Constant.OPERATORID, oldDomain.getField(Constant.OPERATORID), Long.class);
		}
		newDomain.setField(Constant.UPDATETIME, time, Date.class);
	}
	
	public static Object parseFieldValue(Object fieldValue, String fieldType) {
		if (fieldValue != null && fieldType != null) {
			try {
				if (Classes.LONG.getName().equals(fieldType)) {
					if (StringUtils.isEmpty(fieldValue)) {
						return null;
					}
					return Long.valueOf(fieldValue.toString());
				} else if (Classes.DOUBLE.getName().equals(fieldType)) {
					if (StringUtils.isEmpty(fieldValue)) {
						return null;
					}
					return Double.valueOf(fieldValue.toString());
				} else if (Classes.DATE.getName().equals(fieldType)) {
					if (StringUtils.isEmpty(fieldValue)) {
						return null;
					}
					if (fieldValue instanceof Long) {
						return new Date((Long) fieldValue);
					} else if (fieldValue instanceof String) {
						String fieldValueStr = fieldValue.toString();
						if (fieldValueStr.length() == DatePattern.NORM_DATE_PATTERN.length()) {
							return DateUtil.parse(fieldValueStr, FastDateFormat.getInstance(DatePattern.NORM_DATE_PATTERN, TimeZone.getTimeZone("UTC"))).toJdkDate();
						} else {
							fieldValueStr = fieldValueStr.replaceAll("T", " ").replaceAll("Z", "");
							return DateUtil.parse(fieldValueStr, FastDateFormat.getInstance(DatePattern.NORM_DATETIME_MS_PATTERN, TimeZone.getTimeZone("UTC"))).toJdkDate();
						}
					}
				}
			} catch (Exception e) {
				throw new AgtmsException(String.format("Convert %s to %s type failed", fieldValue, fieldType), e);
			}
		}
		return fieldValue;
	}
	
}
