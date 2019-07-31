package net.saisimon.agtms.web.config.handler.field;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.handler.FieldHandler;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;

public abstract class AbstractFieldHandler implements FieldHandler {

	@Override
	public Result validate(Template template, TemplateField field, Object value) {
		if (field != null && SystemUtils.isNotEmpty(value)) {
			String str = value.toString();
			Views view = key();
			if (str.length() > view.getSize()) {
				return ErrorMessage.Common.FIELD_LENGTH_OVERFLOW.messageArgs(field.getFieldTitle(), view.getSize());
			}
		}
		return ResultUtils.simpleSuccess();
	}

	@Override
	public Object masking(Object value) {
		return value;
	}
	
}
