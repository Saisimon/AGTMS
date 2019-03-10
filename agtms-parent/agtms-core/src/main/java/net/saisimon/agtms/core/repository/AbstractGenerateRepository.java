package net.saisimon.agtms.core.repository;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.util.TemplateUtils;

/**
 * 自定义对象抽象 Repository
 * 
 * @author saisimon
 *
 */
@Slf4j
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
	
	protected Domain conversion(Map<String, Object> map) throws GenerateException {
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
		Map<String, Object> caseInsensitiveMap = new CaseInsensitiveMap<>(map);
		ReflectionUtils.doWithLocalFields(domain.getClass(), field -> {
			String fieldName = field.getName();
			Object value = caseInsensitiveMap.get(fieldName);
			if (value != null) {
				if (value instanceof BigInteger) {
					value = ((BigInteger) value).longValue();
				} else if (value instanceof BigDecimal) {
					value = ((BigDecimal) value).doubleValue();
				} else if (value instanceof Integer) {
					value = ((Integer) value).longValue();
				} else if (value instanceof Float) {
					value = ((Float) value).doubleValue();
				} else if (field.getType().isAssignableFrom(Date.class)) {
					if (value instanceof Long) {
						value = new Timestamp((Long) value);
					} else if (value instanceof String) {
						value = DateUtil.parseDate(value.toString()).toTimestamp();
					} else if (value instanceof Date) {
						value = new Timestamp(((Date) value).getTime());
					}
				}
				try {
					field.setAccessible(true);
					field.set(domain, value);
				} catch (Exception e) {
					log.error(domain.getClass().getName() + " set field("+ fieldName +") error, msg : " + e.getMessage());
				}
			}
		});
		return domain;
	}
	
}
