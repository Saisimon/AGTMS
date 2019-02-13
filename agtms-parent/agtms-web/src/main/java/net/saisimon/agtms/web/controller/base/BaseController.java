package net.saisimon.agtms.web.controller.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * 基础控制器
 * 
 * @author saisimon
 *
 */
public abstract class BaseController {
	
	@Autowired
	private MessageSource messageSource;
	@Autowired
	protected HttpServletRequest request;
	@Autowired
	protected HttpServletResponse response;
	
	/**
	 * 根据 code 获取对应的国际化消息
	 * 
	 * @param code 消息代码
	 * @param args 消息参数
	 * @return 消息
	 */
	protected String getMessage(String code, Object... args) {
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
	
}
