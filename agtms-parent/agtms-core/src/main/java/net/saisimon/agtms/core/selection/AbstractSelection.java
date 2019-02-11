package net.saisimon.agtms.core.selection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import net.saisimon.agtms.core.enums.Selections;

public abstract class AbstractSelection implements Selection {
	
	@Autowired
	protected MessageSource messageSource;
	
	protected String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
	
	public abstract Selections key();

}
