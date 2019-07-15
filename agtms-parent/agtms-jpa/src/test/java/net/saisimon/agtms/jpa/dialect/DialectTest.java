package net.saisimon.agtms.jpa.dialect;

import java.util.HashMap;
import java.util.Map;

import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Views;

public abstract class DialectTest {

	protected Map<String, TemplateField> fieldInfoMap() {
		Map<String, TemplateField> fieldInfoMap = new HashMap<>();
		fieldInfoMap.put(columnName(), field());
		return fieldInfoMap;
	}

	protected TemplateField field() {
		TemplateField field = new TemplateField();
		field.setFieldName("field0");
		field.setFieldTitle("Test");
		field.setFieldType(Classes.STRING.getName());
		field.setFilter(false);
		field.setOrdered(0);
		field.setRequired(false);
		field.setSorted(false);
		field.setUniqued(false);
		field.setViews(Views.TEXT.getView());
		return field;
	}

	protected String tableName() {
		return "test";
	}

	protected String columnName() {
		return "column0field0";
	}

}