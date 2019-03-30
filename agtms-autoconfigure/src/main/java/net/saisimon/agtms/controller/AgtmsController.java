package net.saisimon.agtms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.hutool.core.map.MapUtil;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.filter.FilterPageable;
import net.saisimon.agtms.core.domain.filter.FilterRequest;
import net.saisimon.agtms.core.domain.filter.FilterSort;
import net.saisimon.agtms.core.repository.BaseRepository;
import net.saisimon.agtms.core.selection.Selection;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.core.util.TemplateUtils;
import net.saisimon.agtms.scanner.TemplateScanner;
import net.saisimon.agtms.scanner.TemplateScanner.SelectionResolver;
import net.saisimon.agtms.scanner.TemplateScanner.TemplateResolver;

/**
 * API 调用控制器
 * 
 * @author saisimon
 *
 */
@RestController
@RequestMapping("/agtms")
public class AgtmsController {
	
	@Autowired
	private TemplateScanner templateScanner;
	@Autowired
	private ApplicationContext applicationContext;
	
	@PostMapping("/templates")
	public List<Template> templates() {
		return templateScanner.getAllTemplates();
	}
	
	@PostMapping("/{key}/template")
	public Template template(@PathVariable("key") String key) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		TemplateResolver templateResolver = templateScanner.getTemplateResolver(key);
		if (templateResolver == null) {
			return null;
		}
		return templateResolver.getTemplate();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/{key}/selection")
	public Map<?, String> selection(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		SelectionResolver selectionResolver = templateScanner.getSelectionResolver(key);
		if (selectionResolver == null) {
			return null;
		}
		Selection<?> selection = applicationContext.getBean(selectionResolver.getSelectionClass());
		List values = (ArrayList) body.get("values");
		if (!CollectionUtils.isEmpty(values)) {
			return selection.selectValue(values);
		}
		List<String> texts = (ArrayList<String>) body.get("text");
		if (!CollectionUtils.isEmpty(texts)) {
			return selection.selectText(texts);
		}
		String text = (String) body.get("text");
		if (StringUtils.isBlank(text)) {
			return selection.select();
		} else {
			return selection.selectFuzzyText(text);
		}
	}
	
	@PostMapping("/{key}/count")
	public Long count(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		TemplateResolver templateResolver = templateScanner.getTemplateResolver(key);
		if (templateResolver == null) {
			return null;
		}
		BaseRepository<?, ?> repository = applicationContext.getBean(templateResolver.getRepositoryClass());
		if (repository == null) {
			return null;
		}
		Set<String> filters = TemplateUtils.getFilters(templateResolver.getTemplate());
		filters.add(Constant.ID);
		FilterRequest filter = FilterRequest.build(body, filters);
		return repository.count(filter);
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/{key}/findList")
	public List<Map<String, Object>> findListBySort(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		TemplateResolver templateResolver = templateScanner.getTemplateResolver(key);
		if (templateResolver == null) {
			return null;
		}
		BaseRepository<?, ?> repository = applicationContext.getBean(templateResolver.getRepositoryClass());
		if (repository == null) {
			return null;
		}
		Map<String, Object> filterMap = (Map<String, Object>) body.get(Constant.Param.FILTER);
		Set<String> filters = TemplateUtils.getFilters(templateResolver.getTemplate());
		filters.add(Constant.ID);
		FilterRequest filter = FilterRequest.build(filterMap, filters);
		Object propertiesStr = body.get(Constant.Param.PROPERTIES);
		String[] properties = null;
		if (propertiesStr != null) {
			properties = propertiesStr.toString().split(",");
		}
		List<?> entities = null;
		Map<String, Object> pageableMap = (Map<String, Object>) body.get(Constant.Param.PAGEABLE);
		Object sortStr = body.get(Constant.Param.SORT);
		if (sortStr != null) {
			FilterSort sort = FilterSort.build(sortStr.toString());
			entities = repository.findList(filter, sort, properties);
		} else if (pageableMap != null) {
			FilterPageable pageable = FilterPageable.build(pageableMap);
			entities = repository.findList(filter, pageable, properties);
		} else {
			entities = repository.findList(filter, (FilterSort)null, properties);
		}
		List<Map<String, Object>> list = new ArrayList<>();
		if (CollectionUtils.isEmpty(entities)) {
			return list;
		}
		Set<String> fieldNames = TemplateUtils.getFieldNames(templateResolver.getTemplate());
		fieldNames.add(Constant.ID);
		for (Object entity : entities) {
			list.add(toMap(entity, templateResolver.getEntityClass(), fieldNames));
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/{key}/findPage")
	public Map<String, Object> findPage(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		TemplateResolver templateResolver = templateScanner.getTemplateResolver(key);
		if (templateResolver == null) {
			return null;
		}
		BaseRepository<?, ?> repository = applicationContext.getBean(templateResolver.getRepositoryClass());
		if (repository == null) {
			return null;
		}
		Map<String, Object> filterMap = (Map<String, Object>) body.get(Constant.Param.FILTER);
		Map<String, Object> pageableMap = (Map<String, Object>) body.get(Constant.Param.PAGEABLE);
		Set<String> filters = TemplateUtils.getFilters(templateResolver.getTemplate());
		filters.add(Constant.ID);
		FilterRequest filter = FilterRequest.build(filterMap, filters);
		FilterPageable pageable = FilterPageable.build(pageableMap);
		Object propertiesStr = body.get(Constant.Param.PROPERTIES);
		String[] properties = null;
		if (propertiesStr != null) {
			properties = propertiesStr.toString().split(",");
		}
		Page<?> page = repository.findPage(filter, pageable, properties);
		List<?> entities = page.getContent();
		Map<String, Object> result = MapUtil.newHashMap();
		if (CollectionUtils.isEmpty(entities)) {
			result.put("rows", new ArrayList<>(0));
			result.put("total", 0L);
			return result;
		}
		List<Map<String, Object>> list = new ArrayList<>();
		Set<String> fieldNames = TemplateUtils.getFieldNames(templateResolver.getTemplate());
		fieldNames.add(Constant.ID);
		for (Object entity : entities) {
			list.add(toMap(entity, templateResolver.getEntityClass(), fieldNames));
		}
		result.put("rows", list);
		result.put("total", page.getTotalElements());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/{key}/findOne")
	public Map<String, Object> findOne(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		TemplateResolver templateResolver = templateScanner.getTemplateResolver(key);
		if (templateResolver == null) {
			return null;
		}
		BaseRepository<?, ?> repository = applicationContext.getBean(templateResolver.getRepositoryClass());
		if (repository == null) {
			return null;
		}
		Map<String, Object> filterMap = (Map<String, Object>) body.get(Constant.Param.FILTER);
		Set<String> filters = TemplateUtils.getFilters(templateResolver.getTemplate());
		filters.add(Constant.ID);
		FilterRequest filter = FilterRequest.build(filterMap, filters);
		FilterSort sort = null;
		Object sortStr = body.get(Constant.Param.SORT);
		if (sortStr != null) {
			sort = FilterSort.build(sortStr.toString());
		}
		Object propertiesStr = body.get(Constant.Param.PROPERTIES);
		String[] properties = null;
		if (propertiesStr != null) {
			properties = propertiesStr.toString().split(",");
		}
		Optional<?> optional = repository.findOne(filter, sort, properties);
		if (optional.isPresent()) {
			Set<String> fieldNames = TemplateUtils.getFieldNames(templateResolver.getTemplate());
			fieldNames.add(Constant.ID);
			return toMap(optional.get(), templateResolver.getEntityClass(), fieldNames);
		}
		return null;
	}
	
	@PostMapping("/{key}/delete")
	public Long delete(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		TemplateResolver templateResolver = templateScanner.getTemplateResolver(key);
		if (templateResolver == null) {
			return null;
		}
		BaseRepository<?, ?> repository = applicationContext.getBean(templateResolver.getRepositoryClass());
		if (repository == null) {
			return null;
		}
		Set<String> filters = TemplateUtils.getFilters(templateResolver.getTemplate());
		filters.add(Constant.ID);
		FilterRequest filter = FilterRequest.build(body, filters);
		return repository.delete(filter);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/{key}/deleteEntity")
	public void delete(@PathVariable("key") String key, @RequestParam(name="body") String body) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		TemplateResolver templateResolver = templateScanner.getTemplateResolver(key);
		if (templateResolver == null) {
			return;
		}
		BaseRepository repository = applicationContext.getBean(templateResolver.getRepositoryClass());
		if (repository == null) {
			return;
		}
		Object entity = SystemUtils.fromJson(body, templateResolver.getEntityClass());
		repository.delete(entity);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/{key}/saveOrUpdate")
	public Map<String, Object> saveOrUpdate(@PathVariable("key") String key, @RequestParam(name="body") String body) {
		if (StringUtils.isBlank(key)) {
			return null;
		}
		TemplateResolver templateResolver = templateScanner.getTemplateResolver(key);
		if (templateResolver == null) {
			return null;
		}
		BaseRepository repository = applicationContext.getBean(templateResolver.getRepositoryClass());
		if (repository == null) {
			return null;
		}
		Object entity = SystemUtils.fromJson(body, templateResolver.getEntityClass());
		entity = repository.saveOrUpdate(entity);
		if (entity == null) {
			return null;
		}
		Set<String> fieldNames = TemplateUtils.getFieldNames(templateResolver.getTemplate());
		fieldNames.add(Constant.ID);
		return toMap(entity, templateResolver.getEntityClass(), fieldNames);
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("/{key}/batchUpdate")
	public void batchUpdate(@PathVariable("key") String key, @RequestBody Map<String, Object> body) {
		if (StringUtils.isBlank(key)) {
			return;
		}
		TemplateResolver templateResolver = templateScanner.getTemplateResolver(key);
		if (templateResolver == null) {
			return;
		}
		BaseRepository<?, ?> repository = applicationContext.getBean(templateResolver.getRepositoryClass());
		if (repository == null) {
			return;
		}
		Map<String, Object> filterMap = (Map<String, Object>) body.get(Constant.Param.FILTER);
		Set<String> filters = TemplateUtils.getFilters(templateResolver.getTemplate());
		filters.add(Constant.ID);
		FilterRequest filter = FilterRequest.build(filterMap, filters);
		Map<String, Object> updateMap = (Map<String, Object>) body.get(Constant.Param.UPDATE);
		repository.batchUpdate(filter, updateMap);
	}
	
	private Map<String, Object> toMap(Object entity, Class<?> entityClass, Set<String> fieldNames) {
		Map<String, Object> map = MapUtil.newHashMap(fieldNames.size());
		ReflectionUtils.doWithLocalFields(entityClass, field -> {
			if (fieldNames.contains(field.getName())) {
				field.setAccessible(true);
				Object value = field.get(entity);
				if (value != null) {
					map.put(field.getName(), value);
				}
			}
		});
		return map;
	}
	
}
