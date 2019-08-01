package net.saisimon.agtms.web.config.handler.field;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class PasswordFieldHandler extends AbstractFieldHandler {
	
	@Override
	public Object masking(Object value) {
		if (SystemUtils.isEmpty(value)) {
			return value;
		}
		return maskingPassword(value.toString());
	}
	
	@Override
	public Views key() {
		return Views.PASSWORD;
	}
	
	private Object maskingPassword(String password) {
		return "******";
	}

}
