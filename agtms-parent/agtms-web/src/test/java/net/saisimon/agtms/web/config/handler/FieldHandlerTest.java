package net.saisimon.agtms.web.config.handler;

import org.junit.Assert;
import org.junit.Test;

import net.saisimon.agtms.core.domain.entity.Template;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.dto.Result;
import net.saisimon.agtms.core.util.ResultUtils;
import net.saisimon.agtms.web.config.handler.field.EmailFieldHandler;
import net.saisimon.agtms.web.config.handler.field.LinkFieldHandler;
import net.saisimon.agtms.web.config.handler.field.SelectionFieldHandler;

public class FieldHandlerTest {
	
	@Test
	public void testEmailFieldHandler() {
		EmailFieldHandler emailFieldHandler = new EmailFieldHandler();
		Template template = new Template();
		TemplateField field = new TemplateField();
		Object value = null;
		Result result = emailFieldHandler.validate(template, field, value);
		Assert.assertTrue(ResultUtils.isSuccess(result));
		
		value = "aaa";
		result = emailFieldHandler.validate(template, field, value);
		Assert.assertFalse(ResultUtils.isSuccess(result));
		
		value = buildString(513);
		result = emailFieldHandler.validate(template, field, value);
		Assert.assertFalse(ResultUtils.isSuccess(result));
		
		value = "aaa@gmail.com";
		result = emailFieldHandler.validate(template, field, value);
		Assert.assertTrue(ResultUtils.isSuccess(result));
	}
	
	@Test
	public void testLinkFieldHandler() {
		LinkFieldHandler linkFieldHandler = new LinkFieldHandler();
		Template template = new Template();
		TemplateField field = new TemplateField();
		Object value = null;
		Result result = linkFieldHandler.validate(template, field, value);
		Assert.assertTrue(ResultUtils.isSuccess(result));
		
		value = "aaa";
		result = linkFieldHandler.validate(template, field, value);
		Assert.assertFalse(ResultUtils.isSuccess(result));
		
		value = buildString(1025);
		result = linkFieldHandler.validate(template, field, value);
		Assert.assertFalse(ResultUtils.isSuccess(result));
		
		value = "http://www.aaa.com/bbb";
		result = linkFieldHandler.validate(template, field, value);
		Assert.assertTrue(ResultUtils.isSuccess(result));
	}
	
	@Test
	public void testSelectionFieldHandler() {
		SelectionFieldHandler selectionFieldHandler = new SelectionFieldHandler();
		Template template = new Template();
		TemplateField field = new TemplateField();
		Object value = null;
		Result result = selectionFieldHandler.validate(template, field, value);
		Assert.assertTrue(ResultUtils.isSuccess(result));
		
		field.setSelection("aaa");
		value = "aaa";
		result = selectionFieldHandler.validate(template, field, value);
		Assert.assertFalse(ResultUtils.isSuccess(result));
	}
	
	private String buildString(int size) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			builder.append('a');
		}
		return builder.toString();
	}
	
}
