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
public class LinkFieldHandler extends AbstractFieldHandler {

	@Override
	public Result validate(Template template, TemplateField field, Object value) {
		Result result = super.validate(template, field, value);
		if (!ResultUtils.isSuccess(result)) {
			return result;
		}
		return SystemUtils.isEmpty(value) || SystemUtils.isURL(value.toString()) ? result : ErrorMessage.Common.FIELD_FORMAT_NOT_CORRECT.messageArgs(field.getFieldTitle());
	}
	
	@Override
	public Views view() {
		return Views.LINK;
	}

}
