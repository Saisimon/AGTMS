package net.saisimon.agtms.web.config.advice;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

import lombok.extern.slf4j.Slf4j;
import net.saisimon.agtms.core.annotation.International;
import net.saisimon.agtms.core.dto.PageResult;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.dto.SimpleResult;

/**
 * 全局响应消息国际化
 * 
 * @author saisimon
 *
 */
@ControllerAdvice
@Slf4j
public class InternationalResponseBodyAdvice extends AbstractMappingJacksonResponseBodyAdvice {
	
	@Autowired
	private MessageSource messageSource;

	@SuppressWarnings("unchecked")
	@Override
	protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
			MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
		Object resultObj = bodyContainer.getValue();
		if (resultObj instanceof SimpleResult) {
			SimpleResult<Object> result = (SimpleResult<Object>) resultObj;
			SimpleResult<Object> simpleResult = new SimpleResult<>();
			simpleResult.setCode(result.getCode());
			simpleResult.setData(result.getData());
			simpleResult.setMessage(messageSource.getMessage(result.getMessage(), result.getMessageArgs(), LocaleContextHolder.getLocale()));
			resultObj = simpleResult;
		} else if (resultObj instanceof PageResult) {
			PageResult<Object> result = (PageResult<Object>) resultObj;
			PageResult<Object> pageResult = new PageResult<>();
			pageResult.setCode(result.getCode());
			pageResult.setMessage(messageSource.getMessage(result.getMessage(), result.getMessageArgs(), LocaleContextHolder.getLocale()));
			pageResult.setRows(result.getRows());
			pageResult.setTotal(result.getTotal());
			resultObj = pageResult;
		} else if (resultObj instanceof Result) {
			Result result = (Result) resultObj;
			Result res = new Result();
			res.setCode(result.getCode());
			res.setMessage(messageSource.getMessage(result.getMessage(), result.getMessageArgs(), LocaleContextHolder.getLocale()));
			resultObj = res;
		}
		bodyContainer.setValue(international(resultObj));
	}
	
	@SuppressWarnings("unchecked")
	private Object international(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof List) {
			List<Object> list = (List<Object>) obj;
			ListIterator<Object> it = list.listIterator();
			while (it.hasNext()) {
				Object sub = it.next();
				sub = international(sub);
				it.set(sub);
			}
			return list;
		}
		if (obj instanceof Set) {
			Set<Object> set = (Set<Object>) obj;
			try {
				Set<Object> newSet = set.getClass().newInstance();
				for (Object sub : set) {
					newSet.add(international(sub));
				}
				return newSet;
			} catch (InstantiationException | IllegalAccessException e) {
				log.error("create new set failed.", e);
				return set;
			}
		}
		if (obj.getClass().isArray()) {
			int len = Array.getLength(obj);
			for (int i = 0; i < len; i++) {
				Object sub = Array.get(obj, i);
				sub = international(sub);
				Array.set(obj, i, sub);
			}
			return obj;
		}
		if (obj instanceof Map) {
			Map<Object, Object> map = (Map<Object, Object>) obj;
			for (Object key : map.keySet()) {
				Object val = map.get(key);
				val = international(val);
				map.put(key, val);
			}
			return map;
		}
		if (BeanUtils.isSimpleProperty(obj.getClass())) {
			if (obj instanceof String) {
				String code = (String) obj;
				return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
			}
			return obj;
		}
		ReflectionUtils.doWithFields(obj.getClass(), field -> {
			field.setAccessible(true);
			Object property = field.get(obj);
			property = international(property);
			if (property != null) {
				field.set(obj, property);
			}
		},field -> {
			int mod = field.getModifiers();
			return Modifier.isPrivate(mod) && !Modifier.isFinal(mod) && field.getAnnotation(International.class) != null;
		});
		return obj;
	}

}
