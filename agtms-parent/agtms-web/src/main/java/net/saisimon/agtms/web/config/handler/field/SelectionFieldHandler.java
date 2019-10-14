package net.saisimon.agtms.web.config.handler.field;

import org.springframework.stereotype.Component;

import net.saisimon.agtms.core.domain.entity.Selection;
import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.enums.Views;
import net.saisimon.agtms.core.util.AuthUtils;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.core.util.SelectionUtils;
import net.saisimon.agtms.core.util.SystemUtils;
import net.saisimon.agtms.web.constant.ErrorMessage;

@Component
public class SelectionFieldHandler extends AbstractFieldHandler {
	
	@Override
	public Result validate(Template template, TemplateField field, Object value) {
		if (field != null && SystemUtils.isNotEmpty(value)) {
			String selectionSign = field.selectionSign(template.getService());
			Long userId = AuthUtils.getUid();
			if (userId == null) {
				return ErrorMessage.Common.FIELD_FORMAT_NOT_CORRECT.messageArgs(field.getFieldTitle());
			}
			Selection selection = SelectionUtils.getSelection(selectionSign);
			if (selection == null) {
				return ErrorMessage.Common.FIELD_FORMAT_NOT_CORRECT.messageArgs(field.getFieldTitle());
			}
		}
		return ResultUtils.simpleSuccess();
	}

	@Override
	public Views view() {
		return Views.SELECTION;
	}

}
