package net.saisimon.agtms.web.controller.base;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

public abstract class BaseController {
	
	@Autowired
	private MessageSource messageSource;
	@Autowired
	protected HttpServletRequest request;
	
	protected String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
	
}
