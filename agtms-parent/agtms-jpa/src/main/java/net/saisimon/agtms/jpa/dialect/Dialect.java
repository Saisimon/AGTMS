package net.saisimon.agtms.jpa.dialect;

import java.util.Map;

import org.springframework.data.domain.Pageable;

import net.saisimon.agtms.core.domain.entity.Template.TemplateField;

public interface Dialect {
	
	String columnType(TemplateField field);
	
	String buildCreateSql(Map<String, TemplateField> fieldInfoMap, String tableName);
	
	String buildDropSql(String tableName);
	
	String buildAlterAddSql(TemplateField field, String tableName, String columnName);
	
	String buildAlterModifySql(TemplateField field, String tableName, String columnName);
	
	String buildAlterDropSql(String tableName, String columnName);
	
	void wrapPageSql(StringBuilder sql, Pageable pageable);
	
}
