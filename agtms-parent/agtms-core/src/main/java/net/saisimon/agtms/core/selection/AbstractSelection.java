package net.saisimon.agtms.core.selection;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.CollectionUtils;

/**
 * 下拉选项抽象类
 * 
 * @author saisimon
 *
 */
public abstract class AbstractSelection<T> implements Selection<T> {
	
	@Autowired
	protected MessageSource messageSource;
	
	protected String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}

	@Override
	public LinkedHashMap<T, String> selectValue(List<T> values) {
		if (CollectionUtils.isEmpty(values)) {
			return new LinkedHashMap<>();
		}
		LinkedHashMap<T, String> selectMap = select();
		selectMap.entrySet().removeIf(e -> {
			for (T key : values) {
				if (key == null) {
					continue;
				}
				if (e.getKey().toString().equals(key.toString())) {
					return false;
				}
			}
			return true;
		});
		return selectMap;
	}

	@Override
	public LinkedHashMap<T, String> selectText(List<String> texts) {
		if (CollectionUtils.isEmpty(texts)) {
			return new LinkedHashMap<>();
		}
		LinkedHashMap<T, String> selectMap = select();
		selectMap.entrySet().removeIf(e -> {
			return !texts.contains(e.getValue());
		});
		return selectMap;
	}

	@Override
	public LinkedHashMap<T, String> selectFuzzyText(String text) {
		LinkedHashMap<T, String> selectMap = select();
		selectMap.entrySet().removeIf(e -> {
			return !e.getValue().contains(text);
		});
		return selectMap;
	}
	
}