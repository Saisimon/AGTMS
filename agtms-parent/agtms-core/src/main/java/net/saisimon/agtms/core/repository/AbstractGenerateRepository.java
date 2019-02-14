package net.saisimon.agtms.core.repository;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.Template;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.util.StringUtils;
import net.saisimon.agtms.core.util.TemplateUtils;

/**
 * 自定义对象抽象 Repository
 * 
 * @author saisimon
 *
 */
public abstract class AbstractGenerateRepository implements BaseRepository<Domain, Long> {
	
	private ThreadLocal<Template> local = new ThreadLocal<>();
	
	/**
	 * 初始化模版对象
	 * 
	 * @param template 模版对象
	 */
	public void init(Template template) {
		local.set(template);
	}
	
	/**
	 * 获取模版对象
	 * 
	 * @return 模版对象
	 */
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
	
	public Optional<Domain> conversion(Map<String, Object> map) throws GenerateException {
		return conversion(map, null);
	}
	
	public Optional<Domain> conversion(Map<String, Object> map, Map<String, String> mapping) throws GenerateException {
		if (CollectionUtils.isEmpty(map)) {
			return Optional.empty();
		}
		Object idObj = null;
		Domain domain = newGenerate();
		Field idField = ReflectionUtils.findField(domain.getClass(), Constant.ID);
		if (idField != null) {
			if (null != (idObj = map.get("_id"))) {
				map.remove("_id");
			} else if (null != (idObj = map.get(Constant.ID))) {
				map.remove(Constant.ID);
			} else {
				return Optional.empty();
			}
			Class<?> idClass = idField.getType();
			if (idClass == Long.class || idClass == long.class) {
				domain.setField(Constant.ID, Long.valueOf(idObj.toString()), idClass);
			} else if (idClass == String.class) {
				domain.setField(Constant.ID, idObj.toString(), idClass);
			} else {
				domain.setField(Constant.ID, idObj, idClass);
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
				if (val instanceof BigInteger) {
					domain.setField(name, ((BigInteger) val).longValue(), Long.class);
				} else if (val instanceof BigDecimal) {
					domain.setField(name, ((BigDecimal) val).doubleValue(), Double.class);
				} else if (val instanceof Date) {
					domain.setField(name, ((Date) val), Date.class);
				} else {
					domain.setField(name, val, val.getClass());
				}
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
