package net.saisimon.agtms.web.config.advice;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import net.saisimon.agtms.web.service.common.MessageService;

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
	private MessageService messageService;

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
			simpleResult.setMessage(messageService.getMessage(result.getMessage(), result.getMessageArgs()));
			resultObj = simpleResult;
		} else if (resultObj instanceof PageResult) {
			PageResult<Object> result = (PageResult<Object>) resultObj;
			PageResult<Object> pageResult = new PageResult<>();
			pageResult.setCode(result.getCode());
			pageResult.setMessage(messageService.getMessage(result.getMessage(), result.getMessageArgs()));
			pageResult.setRows(result.getRows());
			pageResult.setMore(result.getMore());
			resultObj = pageResult;
		} else if (resultObj instanceof Result) {
			Result result = (Result) resultObj;
			Result res = new Result();
			res.setCode(result.getCode());
			res.setMessage(messageService.getMessage(result.getMessage(), result.getMessageArgs()));
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
			return internationalList((List<Object>) obj);
		}
		if (obj instanceof Set) {
			return internationalSet((Set<Object>) obj);
		}
		if (obj.getClass().isArray()) {
			return internationalArray(obj);
		}
		if (obj instanceof Map) {
			return internationalMap((Map<Object, Object>) obj);
		}
		if (BeanUtils.isSimpleProperty(obj.getClass())) {
			return internationalSimpleProperty(obj);
		}
		internationalOther(obj);
		return obj;
	}
	
	private Object internationalList(List<Object> list) {
		try {
			ListIterator<Object> it = list.listIterator();
			while (it.hasNext()) {
				Object sub = it.next();
				sub = international(sub);
				it.set(sub);
			}
		} catch (UnsupportedOperationException e) {
			// skip
		}
		return list;
	}
	
	private Object internationalSet(Set<Object> set) {
		try {
			@SuppressWarnings("unchecked")
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
	
	private Object internationalArray(Object obj) {
		int len = Array.getLength(obj);
		for (int i = 0; i < len; i++) {
			Object sub = Array.get(obj, i);
			sub = international(sub);
			Array.set(obj, i, sub);
		}
		return obj;
	}
	
	private Object internationalMap(Map<Object, Object> map) {
		try {
			for (Object key : map.keySet()) {
				Object val = map.get(key);
				val = international(val);
				map.put(key, val);
			}
		} catch (UnsupportedOperationException e) {
			// skip
		}
		return map;
	}
	
	private Object internationalSimpleProperty(Object obj) {
		if (obj instanceof String) {
			return messageService.getMessage((String) obj);
		}
		return obj;
	}

	private void internationalOther(Object obj) {
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
	}
	
}
