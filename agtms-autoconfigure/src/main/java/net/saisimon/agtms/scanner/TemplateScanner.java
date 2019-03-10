package net.saisimon.agtms.scanner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.boot.autoconfigure.domain.EntityScanner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import lombok.Data;
import net.saisimon.agtms.annotation.FieldInfo;
import net.saisimon.agtms.annotation.TemplateInfo;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateColumn;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.enums.Functions;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.util.StringUtils;

public class TemplateScanner {
	
	private final Map<String, TemplateResolver> templateResolverMap;
	
	public TemplateScanner() {
		templateResolverMap = new HashMap<>();
	}
	
	public void scan(ApplicationContext applicationContext) {
		EntityScanner scanner = new EntityScanner(applicationContext);
		Set<Class<?>> classes = null;
		try {
			classes = scanner.scan(TemplateInfo.class);
		} catch (ClassNotFoundException e) {
			// skip
		}
		if (CollectionUtils.isEmpty(classes)) {
			return;
		}
		for (Class<?> clazz : classes) {
			TemplateInfo templateInfo = AnnotationUtils.findAnnotation(clazz, TemplateInfo.class);
			if (StringUtils.isBlank(templateInfo.key())) {
				continue;
			}
			Template template = buildTemplate(clazz, templateInfo);
			templateResolverMap.put(templateInfo.key(), new TemplateResolver(template, templateInfo.repository(), clazz));
		}
	}

	private Template buildTemplate(Class<?> clazz, TemplateInfo templateInfo) {
		Template template = new Template();
		int function = 0;
		for (Functions func : templateInfo.functions()) {
			function += func.getCode();
		}
		template.setKey(templateInfo.key());
		template.setNavigationId(templateInfo.navigation());
		template.setFunction(function);
		template.setTitle(templateInfo.title());
		template.setSource("remote");
		Map<FieldInfo, Field> fieldInfoMap = new TreeMap<>((f1, f2) -> {
			if (f1.columnOrdered() == f2.columnOrdered()) {
				return Integer.compare(f1.fieldOrdered(), f2.fieldOrdered());
			}
			return Integer.compare(f1.columnOrdered(), f2.columnOrdered());
		});
		ReflectionUtils.doWithLocalFields(clazz, field -> {
			FieldInfo fieldInfo = AnnotationUtils.findAnnotation(field, FieldInfo.class);
			if (fieldInfo != null) {
				fieldInfoMap.put(fieldInfo, field);
			}
		});
		Set<TemplateColumn> templateColumns = new HashSet<>();
		int ordered = Integer.MIN_VALUE;
		TemplateColumn templateColumn = null;
		for (FieldInfo fieldInfo : fieldInfoMap.keySet()) {
			if (templateColumn == null || fieldInfo.columnOrdered() != ordered) {
				ordered = fieldInfo.columnOrdered();
				templateColumn = new TemplateColumn();
				templateColumn.setColumnName("");
				templateColumn.setOrdered(fieldInfo.columnOrdered());
				String columnTitle = fieldInfo.columnTitle();
				if (StringUtils.isBlank(columnTitle)) {
					columnTitle = fieldInfo.fieldTitle();
				}
				templateColumn.setTitle(columnTitle);
				templateColumns.add(templateColumn);
			}
			Set<TemplateField> templateFields = templateColumn.getFields();
			if (templateFields == null) {
				templateFields = new HashSet<>();
				templateColumn.setFields(templateFields);
			}
			Field field = fieldInfoMap.get(fieldInfo);
			TemplateField templateField = new TemplateField();
			templateField.setDefaultValue(fieldInfo.defaultValue());
			templateField.setFieldName(field.getName());
			templateField.setFieldTitle(fieldInfo.fieldTitle());
			templateField.setFieldType(fieldInfo.fieldType().getName());
			templateField.setFilter(fieldInfo.filter());
			templateField.setHidden(fieldInfo.hidden());
			templateField.setOrdered(fieldInfo.fieldOrdered());
			templateField.setRequired(fieldInfo.required());
			templateField.setSorted(fieldInfo.sorted());
			templateField.setUniqued(fieldInfo.uniqued());
			templateField.setView(fieldInfo.view().getView());
			templateField.setWidth(fieldInfo.width());
			templateFields.add(templateField);
		}
		template.setColumns(templateColumns);
		return template;
	}
	
	public List<Template> getAllTemplates() {
		List<Template> templates = new ArrayList<>();
		for (TemplateResolver templateResolver : templateResolverMap.values()) {
			if (templateResolver.getTemplate() != null) {
				templates.add(templateResolver.getTemplate());
			}
		}
		return templates;
	}
	
	public TemplateResolver getResolver(String key) {
		return templateResolverMap.get(key);
	}
	
	@Data
	public static class TemplateResolver {
		
		private final Template template;
		
		private final Class<? extends BaseRepository<?, ?>> repositoryClass;
		
		private final Class<?> entityClass;
		
		public TemplateResolver(Template template, Class<? extends BaseRepository<?, ?>> repositoryClass, Class<?> entityClass) {
			this.template = template;
			this.repositoryClass = repositoryClass;
			this.entityClass = entityClass;
		}
		
	}
}
