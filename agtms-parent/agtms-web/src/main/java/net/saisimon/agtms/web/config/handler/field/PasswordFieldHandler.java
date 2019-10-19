package net.saisimon.agtms.web.config.handler.field;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.util.SystemUtils;

/**
 * 密码属性字段处理实现类
 * 
 * @author saisimon
 *
 */
@Component
public class PasswordFieldHandler extends AbstractFieldHandler {
	
	@Override
	public Object masking(Object value) {
		if (SystemUtils.isEmpty(value)) {
			return value;
		}
		return "******";
	}
	
	@Override
	public Views view() {
		return Views.PASSWORD;
	}
	
}
