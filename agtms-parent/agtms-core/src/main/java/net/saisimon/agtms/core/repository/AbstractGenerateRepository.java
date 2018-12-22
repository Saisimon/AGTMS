package net.saisimon.agtms.core.repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.TemplateUtils;

public abstract class AbstractGenerateRepository implements BaseRepository<Domain, Long> {
	
	public static final String CREATOR = "creator";
	public static final String CTIME = "ctime";
	public static final String UTIME = "utime";
	
	private ThreadLocal<Template> local = new ThreadLocal<>();
	
	public void init(Template template) {
		local.set(template);
	}
	
	public Template template() {
		Template template = local.get();
		Assert.notNull(template, "uninit");
		return template;
	}
	
	public Domain newGenerate() throws GenerateException {
		Class<? extends Domain> domainClass = TemplateUtils.getDomainClass(template());
		try {
			return domainClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new GenerateException("create " + domainClass + " instance error.", e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List<Domain> conversions(List<Map> list) throws GenerateException {
		return conversions(list, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Domain> conversions(List<Map> list, Map<String, String> mapping) throws GenerateException {
		List<Domain> domains = new ArrayList<>();
		for (Map<String, Object> map : list) {
			Optional<Domain> optional = conversion(map, mapping);
			if (optional.isPresent()) {
				domains.add(optional.get());
			}
		}
		return domains;
	}
	
	public Optional<Domain> conversion(Map<String, Object> map) throws GenerateException {
		return conversion(map, null);
	}
	
	public Optional<Domain> conversion(Map<String, Object> map, Map<String, String> mapping) throws GenerateException {
		if (CollectionUtils.isEmpty(map)) {
			return Optional.empty();
		}
		Object idObj = null;
		Domain domain = newGenerate();
		Field idField = ReflectionUtils.findField(domain.getClass(), "id");
		if (idField != null) {
			if (null != (idObj = map.get("_id"))) {
				map.remove("_id");
			} else if (null != (idObj = map.get("id"))) {
				map.remove("id");
			} else {
				return Optional.empty();
			}
			Class<?> idClass = idField.getType();
			if (idClass == Long.class || idClass == long.class) {
				domain.setField("id", Long.valueOf(idObj.toString()), idClass);
			} else if (idClass == String.class) {
				domain.setField("id", idObj.toString(), idClass);
			} else {
				domain.setField("id", idObj, idClass);
			}
		} else {
			return Optional.empty();
		}
		for (Entry<String, Object> entry : map.entrySet()) {
			Object val = entry.getValue();
			if (val != null) {
				String[] keys = entry.getKey().split("\\.");
				if (keys.length > 1) {
					for (int i = 1; i < keys.length; i++) {
						val = getValue(val, keys[i]);
					}
				}
				String name = entry.getKey();
				String mappingName = null;
				if (mapping != null && StringUtils.isNotBlank(mappingName = mapping.get(name))) {
					name = mappingName;
				}
				domain.setField(name, val, val.getClass());
			}
		}
		return Optional.of(domain);
	}
	
	@SuppressWarnings("unchecked")
	private Object getValue(Object val, String key) {
		if (val == null) {
			return val;
		}
		if (val.getClass().isArray()) {
			Object[] objs = (Object[]) val;
			Object[] results = new Object[objs.length];
			for (int i = 0; i < objs.length; i++) {
				results[i] = getValue(objs[i], key);
			}
			return results;
		} else if (val instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) val;
			return map.get(key);
		} else if (val instanceof List) {
			List<Object> list = (List<Object>) val;
			List<Object> results = new ArrayList<>();
			for (Object obj : list) {
				results.add(getValue(obj, key));
			}
			return results;
		} else {
			return val;
		}
	}
	
}
