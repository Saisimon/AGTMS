package net.saisimon.agtms.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.util.CollectionUtils;

import cn.hutool.core.util.NumberUtil;
import net.saisimon.agtms.core.cache.Cache;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.factory.CacheFactory;
import net.saisimon.agtms.core.factory.TemplateServiceFactory;
import net.saisimon.agtms.core.generate.DomainGenerater;
import net.saisimon.agtms.core.service.TemplateService;

public class TemplateUtils {
	
	public static Map<String, Template> REMOTE_TEMPLATE_MAP = new ConcurrentHashMap<>();
	
	private TemplateUtils() {
		throw new IllegalAccessError();
	}
	
	public static Template getTemplate(Object id, Long operatorId) {
		if (id == null || operatorId == null) {
			return null;
		}
		if (!NumberUtil.isNumber(id.toString())) {
			return REMOTE_TEMPLATE_MAP.get(id.toString());
		}
		String key = String.format(TemplateService.TEMPLATE_KEY, id);
		Cache cache = CacheFactory.get();
		Template template = cache.get(key, Template.class);
		if (template == null) {
			TemplateService templateService = TemplateServiceFactory.get();
			Optional<Template> optional = templateService.findById(Long.valueOf(id.toString()));
			if (optional.isPresent()) {
				template = optional.get();
				cache.set(key, template, TemplateService.TEMPLATE_TIMEOUT);
			}
		}
		if (template != null && operatorId == template.getOperatorId()) {
			return template;
		}
		return null;
	}
	
	public static String getTableName(Template template) {
		return "agtms_generate_" + template.getId();
	}
	
	public static List<String> getTableColumnNames(Template template) {
		List<String> columnNames = new ArrayList<>();
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
	
	public static Set<String> getFieldNames(Template template) {
		Set<String> fieldNames = new HashSet<>();
		fieldNames.add(Constant.ID);
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return fieldNames;
		}
		for (TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			for (TemplateField field : column.getFields()) {
				fieldNames.add(column.getColumnName() + field.getFieldName());
			}
		}
		return fieldNames;
	}
	
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
	
	public static boolean hasSelection(Template template) {
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return false;
		}
		for (TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			for (TemplateField field : column.getFields()) {
				if (Views.SELECTION.getView().equals(field.getView())) {
					return true;
				}
			}
		}
		return false;
	}
	
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
	
	public static Class<Domain> getDomainClass(Template template) throws GenerateException {
		if (template == null) {
			return null;
		}
		String sign = template.sign();
		if (sign == null) {
			return null;
		}
		String generateClassName = DomainGenerater.buildGenerateName(sign);
		return DomainGenerater.generate(template.getOperatorId().toString(), buildFieldMap(template), generateClassName, false);
	}
	
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
	
	public static boolean checkRequired(Template template) {
		if (template == null || StringUtils.isBlank(template.getTitle())) {
			return false;
		}
		if (CollectionUtils.isEmpty(template.getColumns())) {
			return false;
		}
		for (TemplateColumn templateColumn : template.getColumns()) {
			if (StringUtils.isBlank(templateColumn.getTitle())) {
				return false;
			}
			if (CollectionUtils.isEmpty(templateColumn.getFields())) {
				return false;
			}
			for (TemplateField templateField : templateColumn.getFields()) {
				if (StringUtils.isBlank(templateField.getFieldTitle())) {
					return false;
				}
				if (Views.SELECTION.getView().equals(templateField.getView()) && templateField.getSelectionId() == null) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean checkSize(Template template) {
		if (template == null) {
			return false;
		}
		if (CollectionUtils.isEmpty(template.getColumns()) || template.getColumns().size() > 10) {
			return false;
		}
		for (TemplateColumn templateColumn : template.getColumns()) {
			if (CollectionUtils.isEmpty(templateColumn.getFields()) || templateColumn.getFields().size() > 10) {
				return false;
			}
		}
		return true;
	}
	
	public static Map<String, String> getHeadMap(Template template) {
		Map<String, String> headMap = new LinkedHashMap<>();
		if (template == null || CollectionUtils.isEmpty(template.getColumns())) {
			return headMap;
		}
		for (TemplateColumn column : template.getColumns()) {
			if (CollectionUtils.isEmpty(column.getFields())) {
				continue;
			}
			for (TemplateField field : column.getFields()) {
				String head = getHead(headMap, field.getFieldTitle());
				headMap.put(head, column.getColumnName() + field.getFieldName());
			}
		}
		return headMap;
	}
	
	public static List<Integer> getFunctionCodes(Template template) {
		List<Integer> functions = new ArrayList<>();
		if (template == null || template.getFunction() == null || template.getFunction() == 0) {
			return functions;
		}
		Integer function = template.getFunction();
		for (Functions func : Functions.values()) {
			if (func.getCode().equals((function & func.getCode()))) {
				functions.add(func.getCode());
			}
		}
		return functions;
	}
	
	public static List<String> getFunctions(Template template) {
		List<String> functions = new ArrayList<>();
		if (template == null || template.getFunction() == null || template.getFunction() == 0) {
			return functions;
		}
		for (Functions func : Functions.values()) {
			if (hasFunction(template.getFunction(), func)) {
				functions.add(func.getFunction());
			}
		}
		return functions;
	}
	
	public static boolean hasFunction(Template template, Functions func) {
		if (template == null || template.getFunction() == null || template.getFunction() == 0 || func == null) {
			return false;
		}
		return hasFunction(template.getFunction(), func);
	}
	
	public static boolean hasOneOfFunctions(Template template, Functions... funcs) {
		if (template == null || template.getFunction() == null || template.getFunction() == 0 || funcs == null) {
			return false;
		}
		for (Functions func : funcs) {
			if (hasFunction(template.getFunction(), func)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean hasFunction(Integer function, Functions func) {
		return func.getCode().equals((function & func.getCode()));
	}
	
	private static String getHead(Map<String, String> headMap, String head) {
		int idx = 1;
		String str = head;
		while (headMap.containsKey(str)) {
			str = head + "(" + idx++ + ")";
		}
		return str;
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
