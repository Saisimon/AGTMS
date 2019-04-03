package net.saisimon.agtms.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import cn.hutool.core.util.NumberUtil;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.service.RemoteService;
import net.saisimon.agtms.core.service.TemplateService;
import net.saisimon.agtms.core.spring.SpringContext;

/**
 * 模板相关工具类
 * 
 * @author saisimon
 *
 */
public class TemplateUtils {
	
	private TemplateUtils() {
		throw new IllegalAccessError();
	}
	
	/**
	 * 获取模板对象
	 * 
	 * @param key 模板唯一标识
	 * @param operatorId 用户ID
	 * @return 模板对象
	 */
	public static Template getTemplate(Object key, Long operatorId) {
		if (key == null || operatorId == null) {
			return null;
		}
		String sign = key.toString();
		if (!NumberUtil.isLong(sign)) {
			RemoteService remoteService = SpringContext.getBean("remoteService", RemoteService.class);
			if (remoteService == null) {
				return null;
			}
			String[] strs = sign.split("-");
			if (strs.length != 2) {
				return null;
			}
			Template template = remoteService.template(strs[0], strs[1]);
			if (template != null) {
				template.setService(strs[0]);
			}
			return template;
		}
		TemplateService templateService = TemplateServiceFactory.get();
		Optional<Template> optional = templateService.findById(Long.valueOf(sign));
		if (!optional.isPresent()) {
			return null;
		}
		Template template = optional.get();
		if (operatorId.equals(template.getOperatorId())) {
			return template;
		}
		return null;
	}
	
	/**
	 * 获取自定义对象对应的表名
	 * 
	 * @param template 模板对象
	 * @return 表名
	 */
	public static String getTableName(Template template) {
		if (template == null || template.getId() == null) {
			return null;
		}
		return "agtms_generate_" + template.getId();
	}
	
	/**
	 * 获取自定义对象的命名空间
	 * 
	 * @param template 模板对象
	 * @return 命名空间
	 */
	public static String getNamespace(Template template) {
		if (template == null) {
			return null;
		}
		String namespace = "remote";
		if (template.getOperatorId() != null) {
			namespace = template.getOperatorId().toString();
		}
		return namespace;
	}
	
	/**
	 * 获取自定义对象对应的属性名集合
	 * 
	 * @param template 模板对象
	 * @return 属性名集合
	 */
	public static Set<String> getFieldNames(Template template) {
		Set<String> columnNames = new HashSet<>();
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return columnNames;
		}
		for (TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			for (TemplateField field : column.getFields()) {
				columnNames.add(column.getColumnName() + field.getFieldName());
			}
		}
		return columnNames;
	}
	
	/**
	 * 获取自定义对象属性名与模板属性的映射
	 * 
	 * @param template 模板对象
	 * @return 自定义对象属性名与模板属性的映射
	 */
	public static Map<String, TemplateField> getFieldInfoMap(Template template) {
		Map<String, TemplateField> fieldInfoMap = new LinkedHashMap<>();
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return fieldInfoMap;
		}
		for (TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			for (TemplateField field : column.getFields()) {
				fieldInfoMap.put(column.getColumnName() + field.getFieldName(), field);
			}
		}
		return fieldInfoMap;
	}
	
	/**
	 * 获取模板中是否包含下拉列表
	 * 
	 * @param template 模板对象
	 * @return 是否包含下拉列表
	 */
	public static boolean hasSelection(Template template) {
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return false;
		}
		for (TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			for (TemplateField field : column.getFields()) {
				if (Views.SELECTION.getView().equals(field.getViews())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 获取模板中的必填属性集合
	 * 
	 * @param template 模板对象
	 * @return 必填属性集合
	 */
	public static Set<String> getRequireds(Template template) {
		Set<String> requireds = new HashSet<>();
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return requireds;
		}
		for (TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			for (TemplateField field : column.getFields()) {
				String fieldName = column.getColumnName() + field.getFieldName();
				if (field.getRequired()) {
					requireds.add(fieldName);
				}
				if (field.getRequired()) {
					if (!requireds.contains(fieldName)) {
						requireds.add(fieldName);
					}
				}
			}
		}
		return requireds;
	}
	
	/**
	 * 获取模板中的唯一属性集合
	 * 
	 * @param template 模板对象
	 * @return 唯一属性集合
	 */
	public static Set<String> getUniques(Template template) {
		Set<String> uniques = new HashSet<>();
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return uniques;
		}
		for (TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			for (TemplateField field : column.getFields()) {
				if (field.getUniqued()) {
					uniques.add(column.getColumnName() + field.getFieldName());
				}
			}
		}
		return uniques;
	}
	
	/**
	 * 获取模板中的有筛选条件的属性集合
	 * 
	 * @param template 模板对象
	 * @return 有筛选条件的属性集合
	 */
	public static Set<String> getFilters(Template template) {
		Set<String> filters = new HashSet<>();
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return filters;
		}
		for (TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			for (TemplateField field : column.getFields()) {
				if (field.getFilter()) {
					filters.add(column.getColumnName() + field.getFieldName());
				}
			}
		}
		return filters;
	}
	
	/**
	 * 根据指定模板获取自定义对象类型
	 * 
	 * @param template模板对象
	 * @return 自定义对象类型
	 * @throws GenerateException 生成自定义对象类型异常
	 */
	public static Class<Domain> getDomainClass(Template template) throws GenerateException {
		if (template == null) {
			return null;
		}
		String sign = template.sign();
		if (sign == null) {
			return null;
		}
		String generateClassName = DomainGenerater.buildGenerateName(sign);
		return DomainGenerater.generate(getNamespace(template), buildFieldMap(template), generateClassName);
	}
	
	/**
	 * 获取自定义对象属性名称与类型名称的映射
	 * 
	 * @param template 模板对象
	 * @return 自定义对象属性名称与类型名称的映射
	 */
	public static Map<String, String> buildFieldMap(Template template) {
		Map<String, String> fieldMap = new LinkedHashMap<>();
		fieldMap.put(Constant.ID, Long.class.getName());
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return fieldMap;
		}
		for (TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			for (TemplateField field : column.getFields()) {
				Class<?> parseClass = parseClass(field.getFieldType());
				fieldMap.put(column.getColumnName() + field.getFieldName(), parseClass.getName());
			}
		}
		return fieldMap;
	}
	
	/**
	 * 检查模板中的必填项
	 * 
	 * @param template 模板对象
	 * @return 是否检查通过
	 */
	public static boolean checkRequired(Template template) {
		if (template == null || SystemUtils.isBlank(template.getTitle())) {
			return false;
		}
		if (CollectionUtils.isEmpty(template.getColumns()) || template.getColumns().size() > 10) {
			return false;
		}
		for (TemplateColumn templateColumn : template.getColumns()) {
			if (templateColumn == null) {
				return false;
			}
			if (SystemUtils.isBlank(templateColumn.getTitle())) {
				return false;
			}
			if (CollectionUtils.isEmpty(templateColumn.getFields()) || templateColumn.getFields().size() > 10) {
				return false;
			}
			for (TemplateField templateField : templateColumn.getFields()) {
				if (SystemUtils.isBlank(templateField.getFieldTitle())) {
					return false;
				}
				if (Views.SELECTION.getView().equals(templateField.getViews()) && templateField.selectionSign(template.getService()) == null) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * 获取模板所包含的功能值集合
	 * 
	 * @param template 模板对象
	 * @return 功能值集合
	 */
	public static List<Integer> getFunctionCodes(Template template) {
		List<Integer> functions = new ArrayList<>();
		if (template == null || template.getFunctions() == null || template.getFunctions() == 0) {
			return functions;
		}
		Integer function = template.getFunctions();
		for (Functions func : Functions.values()) {
			if (func.getCode().equals((function & func.getCode()))) {
				functions.add(func.getCode());
			}
		}
		return functions;
	}
	
	/**
	 * 获取模板所包含的功能名称集合
	 * 
	 * @param template 模板对象
	 * @return 功能名称集合
	 */
	public static List<String> getFunctions(Template template) {
		List<String> functions = new ArrayList<>();
		if (template == null || template.getFunctions() == null || template.getFunctions() == 0) {
			return functions;
		}
		for (Functions func : Functions.values()) {
			if (hasFunction(template.getFunctions(), func)) {
				functions.add(func.getFunction());
			}
		}
		return functions;
	}
	
	/**
	 * 指定模板中是否包含指定的功能
	 * 
	 * @param template 模板对象
	 * @param func 待判断的功能
	 * @return 是否包含指定的功能
	 */
	public static boolean hasFunction(Template template, Functions func) {
		if (template == null || template.getFunctions() == null || func == null) {
			return false;
		}
		return hasFunction(template.getFunctions(), func);
	}
	
	/**
	 * 指定模板中是否包含指定的功能集合中的某一个
	 * 
	 * @param template 模板对象
	 * @param funcs 待判断的功能集合
	 * @return 是否包含指定的功能集合中的某一个
	 */
	public static boolean hasOneOfFunctions(Template template, Functions... funcs) {
		if (template == null || template.getFunctions() == null || template.getFunctions() == 0 || funcs == null) {
			return false;
		}
		for (Functions func : funcs) {
			if (hasFunction(template.getFunctions(), func)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean hasFunction(Integer function, Functions func) {
		return func.getCode().equals((function & func.getCode()));
	}
	
	private static Class<?> parseClass(String className) {
		Class<?> clazz;
		switch (className) {
		case "int":
			clazz = Integer.class;
			break;
		case "double":
			clazz = Double.class;
			break;
		case "long":
			clazz = Long.class;
			break;
		case "date":
			clazz = Date.class;
			break;
		default:
			clazz = String.class;
			break;
		}
		return clazz;
	}
	
}
