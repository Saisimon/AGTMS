package net.saisimon.agtms.web.config.handler.field;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.util.SystemUtils;

@Component
public class PhoneFieldHandler extends AbstractFieldHandler {
	
	@Override
	public Object masking(Object value) {
		if (SystemUtils.isEmpty(value)) {
			return value;
		}
		return maskingPhone(value.toString());
	}

	@Override
	public Views view() {
		return Views.PHONE;
	}
	
	private static String maskingPhone(String phone) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < phone.length(); i++) {
			if (i > 2 && i < 7) {
				buffer.append('*');
			} else {
				buffer.append(phone.charAt(i));
			}
		}
		return buffer.toString();
	}

}
