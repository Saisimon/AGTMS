package net.saisimon.agtms.web.config.handler.field;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;

@Component
public class EmailFieldHandler extends AbstractFieldHandler {

	@Override
	public Result validate(Template template, TemplateField field, Object value) {
		Result result = super.validate(template, field, value);
		if (!ResultUtils.isSuccess(result)) {
			return result;
		}
		return SystemUtils.isEmpty(value) || SystemUtils.isEmail(value.toString()) ? result : ErrorMessage.Common.FIELD_FORMAT_NOT_CORRECT.messageArgs(field.getFieldTitle());
	}
	
	@Override
	public Object masking(Object value) {
		if (SystemUtils.isEmpty(value)) {
			return value;
		}
		return maskingEmail(value.toString());
	}
	
	@Override
	public Views view() {
		return Views.EMAIL;
	}
	
	private String maskingEmail(String email) {
		StringBuffer buffer = new StringBuffer();
		boolean emailSignal = false;
		for (int i = 0; i < email.length(); i++) {
			char c = email.charAt(i);
			if (c == '@') {
				emailSignal = true;
			}
			if (i > 2 && (!emailSignal && c != '@')) {
				buffer.append('*');
			} else {
				buffer.append(c);
			}
		}
		return buffer.toString();
	}

}
