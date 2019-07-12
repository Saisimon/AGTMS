package net.saisimon.agtms.jpa.dialect;

import java.util.Map;

import org.springframework.data.domain.Pageable;

import net.saisimon.agtms.core.domain.entity.Template.TemplateField;

public interface Dialect {
	
	String columnType(TemplateField field);
	
	String buildCreateSQL(Map<String, TemplateField> fieldInfoMap, String tableName);
	
	String buildDropSQL(String tableName);
	
	String buildAlterAddSQL(TemplateField field, String tableName, String columnName);
	
	String buildAlterModifySQL(TemplateField field, String tableName, String columnName);
	
	String buildAlterDropSQL(String tableName, String columnName);
	
	void wrapPageSQL(StringBuilder sql, Pageable pageable);
	
}
