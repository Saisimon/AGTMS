package net.saisimon.agtms.core.repository;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

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
	
	public Domain conversion(Map<String, Object> map) throws GenerateException {
		return conversion(map, null);
	}
	
	public Domain conversion(Map<String, Object> map, Map<String, String> mapping) throws GenerateException {
		if (CollectionUtils.isEmpty(map)) {
			return null;
		}
		Domain domain = newGenerate();
		Field idField = ReflectionUtils.findField(domain.getClass(), Constant.ID);
		if (idField == null) {
			return null;
		}
		Object idObj = map.remove("_id");
		if (idObj == null) {
			idObj = map.remove(Constant.ID);
		}
		if (idObj == null) {
			return null;
		}
		domain.setField(Constant.ID, Long.valueOf(idObj.toString()), Long.class);
		for (Entry<String, Object> entry : map.entrySet()) {
			Object val = entry.getValue();
			if (val != null) {
				String name = entry.getKey();
				name = name.toLowerCase();
				if (Constant.OPERATORID.equalsIgnoreCase(name)) {
					name = Constant.OPERATORID;
				} else if (Constant.CREATETIME.equalsIgnoreCase(name)) {
					name = Constant.CREATETIME;
				} else if (Constant.UPDATETIME.equalsIgnoreCase(name)) {
					name = Constant.UPDATETIME;
				}
				if (mapping != null) {
					String mappingName = mapping.get(name);
					if (StringUtils.isNotBlank(mappingName)) {
						name = mappingName;
					}
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
		return domain;
	}
	
}
