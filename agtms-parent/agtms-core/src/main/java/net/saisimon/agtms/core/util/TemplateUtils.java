package net.saisimon.agtms.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.CollectionUtils;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.domain.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.Template.TemplateField;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.generate.DomainGenerater;

public class TemplateUtils {
	
	private TemplateUtils() {
		throw new IllegalAccessError();
	}
	
	public static String getTableName(Template template) {
		return "agtms_generate_" + template.getId();
	}
	
	public static List<String> getTableColumnNames(Template template) {
		List<String> columnNames = new ArrayList<>();
		if (!CollectionUtils.isEmpty(template.getColumns())) {
			for (TemplateColumn column : template.getColumns()) {
				if (!CollectionUtils.isEmpty(column.getFields())) {
					for (TemplateField field : column.getFields()) {
						String fieldName = column.getColumnName() + field.getFieldName();
						columnNames.add(fieldName);
					}
				}
			}
		}
		return columnNames;
	}
	
	public static Map<String, TemplateField> getFieldInfoMap(Template template) {
		Map<String, TemplateField> fieldInfoMap = new LinkedHashMap<>();
		if (!CollectionUtils.isEmpty(template.getColumns())) {
			for (TemplateColumn column : template.getColumns()) {
				if (!CollectionUtils.isEmpty(column.getFields())) {
					for (TemplateField field : column.getFields()) {
						String fieldName = column.getColumnName() + field.getFieldName();
						fieldInfoMap.put(fieldName, field);
					}
				}
			}
		}
		return fieldInfoMap;
	}
	
	public static Set<String> getRequireds(Template template) {
		Set<String> requireds = new HashSet<>();
		if (!CollectionUtils.isEmpty(template.getColumns())) {
			for (TemplateColumn column : template.getColumns()) {
				if (!CollectionUtils.isEmpty(column.getFields())) {
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
			}
		}
		return requireds;
	}
	
	public static Set<String> getUniques(Template template) {
		Set<String> uniques = new HashSet<>();
		if (!CollectionUtils.isEmpty(template.getColumns())) {
			for (TemplateColumn column : template.getColumns()) {
				if (!CollectionUtils.isEmpty(column.getFields())) {
					for (TemplateField field : column.getFields()) {
						String fieldName = column.getColumnName() + field.getFieldName();
						if (field.getUniqued()) {
							uniques.add(fieldName);
						}
					}
				}
			}
		}
		return uniques;
	}
	
	public static Class<Domain> getDomainClass(Template template) throws GenerateException {
		String generateClassName = DomainGenerater.buildGenerateName(template.getId());
		return DomainGenerater.generate(TokenUtils.getUserInfo().getLoginName(), buildFieldMap(template), generateClassName, false);
	}
	
	public static Map<String, String> buildFieldMap(Template template) {
		Map<String, String> fieldMap = new LinkedHashMap<>();
		if (!CollectionUtils.isEmpty(template.getColumns())) {
			for (TemplateColumn column : template.getColumns()) {
				if (column.getColumnId() < 0) {
					continue;
				}
				if (column.getColumnName() == null) {
					column.setColumnName("column" + column.getColumnId());
				}
				if (!CollectionUtils.isEmpty(column.getFields())) {
					for (TemplateField field : column.getFields()) {
						if (field.getFieldId() < 0) {
							continue;
						}
						if (field.getFieldName() == null) {
							field.setFieldName("field" + field.getFieldId());
						}
						if (StringUtils.isBlank(field.getFieldType())) {
							field.setFieldType(Classes.STRING.getClazz());
						}
						if (StringUtils.isBlank(field.getView())) {
							field.setView(Views.TEXT.getView());
						}
						fieldMap.put(column.getColumnName() + field.getFieldName(), SystemUtils.handleFieldType(field.getFieldType()));
					}
				}
			}
		}
		fieldMap.put("id", Long.class.getName());
		return fieldMap;
	}
	
	public static Map<String, String> getHeadMap(Template template) {
		Map<String, String> headMap = new LinkedHashMap<>();
		if (!CollectionUtils.isEmpty(template.getColumns())) {
			for (TemplateColumn column : template.getColumns()) {
				if (!CollectionUtils.isEmpty(column.getFields())) {
					for (TemplateField field : column.getFields()) {
						String head = getHead(headMap, field.getFieldTitle());
						headMap.put(head, column.getColumnName() + field.getFieldName());
					}
				}
			}
		}
		return headMap;
	}
	
	private static String getHead(Map<String, String> headMap, String head) {
		int idx = 1;
		String str = head;
		while (headMap.containsKey(str)) {
			str = head + "(" + idx++ + ")";
		}
		return str;
	}
	
}
