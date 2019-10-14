package net.saisimon.agtms.core.util;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import cn.hutool.core.util.NumberUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.factory.FieldHandlerFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.handler.FieldHandler;
import net.saisimon.agtms.core.service.RemoteService;
import net.saisimon.agtms.core.spring.SpringContext;

/**
 * 模板相关工具类
 * 
 * @author saisimon
 *
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TemplateUtils {
	
	/**
	 * 获取模板对象
	 * 
	 * @param key 模板唯一标识
	 * @param operatorIds 用户ID集合
	 * @return 模板对象
	 */
	public static Template getTemplate(Object key) {
		if (key == null) {
			return null;
		}
		String sign = key.toString();
		if (!NumberUtil.isLong(sign)) {
			return getRemoteTemplate(sign);
		}
		Optional<Template> optional = TemplateServiceFactory.get().findById(Long.valueOf(sign));
		if (!optional.isPresent()) {
			return null;
		}
		return optional.get();
	}
	
	/**
	 * 获取远程模板对象
	 * 
	 * @param key 模板唯一标识
	 * @return 模板对象
	 */
	public static Template getRemoteTemplate(String key) {
		RemoteService remoteService = SpringContext.getBean("remoteService", RemoteService.class);
		if (remoteService == null) {
			return null;
		}
		String[] strs = key.split("-");
		if (strs.length != 2) {
			return null;
		}
		Template template = remoteService.template(strs[0], strs[1]);
		if (template != null) {
			template.setService(strs[0]);
		}
		return template;
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
			if (!checkRequired(template.getService(), templateColumn)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 检查模板列中的必填项
	 * 
	 * @param service 模板对应服务名
	 * @param templateColumn 模板列对象
	 * @return 是否检查通过
	 */
	public static boolean checkRequired(String service, TemplateColumn templateColumn) {
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
			if (!checkRequired(service, templateField)) {
				return false;
			}
			Classes fieldType = SystemUtils.parseEnum(Classes.class, templateField.getFieldType());
			if (fieldType == null) {
				return false;
			}
			Views views = SystemUtils.parseEnum(Views.class, templateField.getViews());
			if (views == null) {
				return false;
			}
			if (fieldType != Classes.STRING) {
				templateField.setViews(Views.TEXT.getKey());
			}
		}
		return true;
	}
	
	/**
	 * 检查模板属性中的必填项
	 * 
	 * @param service 模板对应服务名
	 * @param templateField 模板属性对象
	 * @return 是否检查通过
	 */
	public static boolean checkRequired(String service, TemplateField templateField) {
		if (templateField == null) {
			return false;
		}
		if (SystemUtils.isBlank(templateField.getFieldTitle())) {
			return false;
		}
		return !(Views.SELECTION.getKey().equals(templateField.getViews()) && templateField.selectionSign(service) == null);
	}
	
	/**
	 * 验证属性值是否符合要求
	 * 
	 * @param field 属性对象
	 * @param value 属性值
	 * @return 验证结果
	 */
	public static Result validate(Template template, TemplateField field, Object value) {
		FieldHandler handler = FieldHandlerFactory.getHandler(field.getViews());
		if (handler != null) {
			return handler.validate(template, field, value);
		}
		return ResultUtils.simpleSuccess();
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
