package net.saisimon.agtms.web.selection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import net.saisimon.agtms.core.selection.Selection;

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
	
}
