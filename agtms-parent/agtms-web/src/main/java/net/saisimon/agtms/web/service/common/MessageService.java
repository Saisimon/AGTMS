package net.saisimon.agtms.web.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/**
 * 消息服务
 * 
 * @author saisimon
 *
 */
@Service
public class MessageService {
	
	@Autowired
	private MessageSource messageSource;
	
	/**
	 * 根据 code 获取对应的国际化消息
	 * 
	 * @param code 消息代码
	 * @param args 消息参数
	 * @return 消息
	 */
	public String getMessage(String code, Object... args) {
		if (code == null) {
			return null;
		}
		return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
	}
	
}
