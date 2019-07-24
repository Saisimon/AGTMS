package net.saisimon.agtms.core.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.Domain;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.exception.GenerateException;
import net.saisimon.agtms.core.generate.DomainGenerater;
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
	
	@Autowired
	private DomainGenerater domainGenerater;
	
	/**
	 * 初始化模版对象
	 * 
	 * @param template 模版对象
	 */
	public void init(final Template template) {
		local.set(template);
	}
	
	/**
	 * 清除模版对象
	 */
	public void remove() {
		local.remove();
	}
	
	/**
	 * 获取模版对象
	 * 
	 * @return 模版对象
	 */
	public Template template() {
		Template template = local.get();
		Assert.notNull(template, "未初始化模板");
		return template;
	}
	
	public Domain newGenerate() throws GenerateException {
		Class<? extends Domain> domainClass = getDomainClass(template());
		try {
			return domainClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new GenerateException("创建 " + domainClass + " 实例失败", e);
		}
	}
	
	private Class<Domain> getDomainClass(Template template) throws GenerateException {
		if (template == null) {
			return null;
		}
		String sign = template.sign();
		if (sign == null) {
			return null;
		}
		String generateClassName = domainGenerater.buildGenerateName(sign);
		return domainGenerater.generate(TemplateUtils.getNamespace(template), TemplateUtils.buildFieldMap(template), generateClassName);
	}
	
	protected Domain conversion(Map<String, Object> map) throws GenerateException {
		if (CollectionUtils.isEmpty(map)) {
			return null;
		}
		Long id = getId(map);
		if (id == null) {
			return null;
		}
		Domain domain = newGenerate();
		domain.setField(Constant.ID, id, Long.class);
		Map<String, Object> caseInsensitiveMap = new CaseInsensitiveMap<>(map);
		ReflectionUtils.doWithLocalFields(domain.getClass(), field -> {
			String fieldName = field.getName();
			Object value = caseInsensitiveMap.get(fieldName);
			if (value != null) {
				value = convertValue(value, field.getType());
				try {
					field.setAccessible(true);
					field.set(domain, value);
				} catch (Exception e) {
					log.error(domain.getClass().getName() + " set field("+ fieldName +") error", e);
				}
			}
		});
		return domain;
	}

	private Long getId(Map<String, Object> map) {
		Object idObj = map.remove(Constant.MONGODBID);
		if (idObj == null) {
			idObj = map.remove(Constant.ID);
		}
		if (idObj == null) {
			return null;
		}
		return Long.valueOf(idObj.toString());
	}
	
	private Object convertValue(Object value, Class<?> type) {
		Object val = value;
		if (value instanceof BigInteger) {
			val = ((BigInteger) value).longValue();
		} else if (value instanceof BigDecimal) {
			val = ((BigDecimal) value).doubleValue();
		} else if (value instanceof Integer) {
			val = ((Integer) value).longValue();
		} else if (value instanceof Float) {
			val = ((Float) value).doubleValue();
		} else if (type.isAssignableFrom(Date.class)) {
			if (value instanceof Long) {
				val = new Timestamp((Long) value);
			} else if (value instanceof String) {
				val = DateUtil.parseDate(value.toString()).toTimestamp();
			} else if (value instanceof Date) {
				val = new Timestamp(((Date) value).getTime());
			}
		}
		return val;
	}
	
}
