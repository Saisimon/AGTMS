package net.saisimon.agtms.jpa.dialect;

import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.domain.Pageable;

import net.saisimon.agtms.core.constant.Constant;
import net.saisimon.agtms.core.domain.entity.Template.TemplateField;
import net.saisimon.agtms.core.enums.Classes;
import net.saisimon.agtms.core.enums.Views;

public class MysqlDialect implements Dialect {

	@Override
	public String buildCreateSql(Map<String, TemplateField> fieldInfoMap, String tableName) {
		String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
		sql += Constant.ID + " BIGINT NOT NULL AUTO_INCREMENT, ";
		for (Entry<String, TemplateField> entry : fieldInfoMap.entrySet()) {
			String fieldname = entry.getKey();
			TemplateField field = entry.getValue();
			sql += fieldname;
			sql += columnType(field) + ", ";
		}
		sql += Constant.OPERATORID + " BIGINT NOT NULL, ";
		sql += Constant.CREATETIME + " DATETIME, ";
		sql += Constant.UPDATETIME + " DATETIME, ";
		sql += "PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8";
		return sql;
	}
	
	@Override
	public String buildDropSql(String tableName) {
		return "DROP TABLE IF EXISTS " + tableName;
	}

	@Override
	public String buildAlterAddSql(TemplateField field, String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " ADD " + columnName + columnType(field);
	}

	@Override
	public String buildAlterModifySql(TemplateField field, String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " MODIFY COLUMN " + columnName + columnType(field);
	}

	@Override
	public String buildAlterDropSql(String tableName, String columnName) {
		return "ALTER TABLE " + tableName + " DROP COLUMN " + columnName;
	}

	@Override
	public String columnType(TemplateField field) {
		String column = "";
		if (Classes.LONG.getName().equals(field.getFieldType())) {
			column = " BIGINT";
		} else if (Classes.DOUBLE.getName().equals(field.getFieldType())) {
			column = " DOUBLE";
		} else if (Classes.DATE.getName().equals(field.getFieldType())) {
			column = " DATETIME";
		} else if (Views.TEXTAREA.getView().equals(field.getViews())) {
			column = " TEXT";
		} else if (Views.PHONE.getView().equals(field.getViews())) {
			column = " VARCHAR(32)";
		} else if (Views.EMAIL.getView().equals(field.getViews())) {
			column = " VARCHAR(256)";
		} else if (Views.LINK.getView().equals(field.getViews())) {
			column = " VARCHAR(1024)";
		} else if (Views.ICON.getView().equals(field.getViews())) {
			column = " VARCHAR(64)";
		} else if (Views.IMAGE.getView().equals(field.getViews())) {
			column = " VARCHAR(64)";
		} else {
			column = " VARCHAR(512)";
		}
		if (field.getRequired()) {
			column += " NOT NULL";
		}
		return column;
	}
	
	@Override
	public void wrapPageSql(StringBuilder sql, Pageable pageable) {
		sql.append(" LIMIT ").append(pageable.getOffset()).append(", ").append(pageable.getPageSize());
	}

}
