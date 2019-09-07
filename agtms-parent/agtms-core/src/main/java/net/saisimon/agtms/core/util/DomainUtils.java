package net.saisimon.agtms.core.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.util.StringUtils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.map.MapUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.encrypt.Encryptor;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.exception.AgtmsException;
import net.saisimon.agtms.core.factory.EncryptorFactory;
import net.saisimon.agtms.core.factory.FieldHandlerFactory;
import net.saisimon.agtms.core.handler.FieldHandler;
import net.saisimon.agtms.core.property.AgtmsProperties;
import net.saisimon.agtms.core.spring.SpringContext;

/**
 * Domain 相关工具类
 * 
 * @author saisimon
 *
 */
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
				if (Classes.LONG.getKey().equals(fieldType)) {
					if (StringUtils.isEmpty(fieldValue)) {
						return null;
					}
					return Long.valueOf(fieldValue.toString());
				} else if (Classes.DOUBLE.getKey().equals(fieldType)) {
					if (StringUtils.isEmpty(fieldValue)) {
						return null;
					}
					return Double.valueOf(fieldValue.toString());
				} else if (Classes.DATE.getKey().equals(fieldType)) {
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
	
	public static Object encrypt(Object value) {
		try {
			return crypt(value, true);
		} catch (Exception e) {
			log.warn("加密失败");
			return null;
		}
	}
	
	public static Object decrypt(Object value) {
		try {
			return crypt(value, false);
		} catch (Exception e) {
			log.warn("解密失败");
			return null;
		}
	}
	
	public static List<Map<String, Object>> conversions(Template template, List<Domain> domains, Collection<Long> operatorIds) {
		return conversions(TemplateUtils.getFieldInfoMap(template), template.getService(), domains, operatorIds);
	}
	
	public static List<Map<String, Object>> conversions(Map<String, TemplateField> fieldInfoMap, String service, List<Domain> domains, Collection<Long> operatorIds) {
		List<Map<String, Object>> datas = new ArrayList<>(domains.size());
		Map<String, Set<String>> valueMap = MapUtil.newHashMap();
		for (Domain domain : domains) {
			datas.add(conversion(fieldInfoMap, valueMap, domain));
		}
		Map<String, Map<String, String>> map = MapUtil.newHashMap();
		for (Map.Entry<String, Set<String>> entry : valueMap.entrySet()) {
			String fieldName = entry.getKey();
			TemplateField templateField = fieldInfoMap.get(fieldName);
			Map<String, String> textMap = SelectionUtils.getSelectionValueTextMap(templateField.selectionSign(service), entry.getValue(), operatorIds);
			map.put(fieldName, textMap);
		}
		for (Map<String, Object> data : datas) {
			for (Map.Entry<String, Map<String, String>> entry : map.entrySet()) {
				String fieldName = entry.getKey();
				Map<String, String> textMap = entry.getValue();
				Object value = data.get(fieldName);
				if (value == null) {
					continue;
				}
				String text = textMap.get(value.toString());
				data.put(fieldName, text);
			}
		}
		return datas;
	}

	private static Map<String, Object> conversion(Map<String, TemplateField> fieldInfoMap, Map<String, Set<String>> valueMap, Domain domain) {
		Map<String, Object> data = MapUtil.newHashMap();
		for (Map.Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldName = entry.getKey();
			TemplateField templateField = entry.getValue();
			Object value = domain.getField(fieldName);
			if (value == null) {
				continue;
			}
			if (Views.SELECTION.getKey().equals(templateField.getViews())) {
				Set<String> values = valueMap.get(fieldName);
				if (values == null) {
					values = new HashSet<>();
					valueMap.put(fieldName, values);
				}
				values.add(value.toString());
			}
			if (Views.PASSWORD.getKey().equals(templateField.getViews())) {
				value = decrypt(value);
			}
			value = marking(value, templateField);
			data.put(fieldName, value);
		}
		data.put(Constant.ID, domain.getField(Constant.ID));
		data.put(Constant.OPERATORID, domain.getField(Constant.OPERATORID));
		data.put(Constant.CREATETIME, domain.getField(Constant.CREATETIME));
		data.put(Constant.UPDATETIME, domain.getField(Constant.UPDATETIME));
		return data;
	}
	
	private static Object marking(Object value, TemplateField templateField) {
		FieldHandler handler = FieldHandlerFactory.getHandler(templateField.getViews());
		if (handler == null) {
			return value;
		}
		return handler.masking(value);
	}
	
	private static Object crypt(Object value, boolean encrypt) throws Exception {
		Object val = value;
		if (val == null) {
			return val;
		}
		AgtmsProperties agtmsProperties = SpringContext.getBean("agtmsProperties", AgtmsProperties.class);
		Encryptor encryptor = EncryptorFactory.get(agtmsProperties.getEncryptorAlgorithm());
		if (encryptor == null) {
			return val;
		}
		if (encrypt) {
			return encryptor.encrypt(val.toString());
		} else {
			return encryptor.decrypt(val.toString());
		}
	}
	
}
